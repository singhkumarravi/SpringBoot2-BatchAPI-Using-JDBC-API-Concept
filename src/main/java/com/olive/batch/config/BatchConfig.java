package com.olive.batch.config;

import java.util.Date;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.olive.product.Product;
import com.olive.utils.TimeUtils;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	//creating the ItemReader object
    @Bean
	public ItemReader<Product> read(){
    	System.out.println("Item Reader Started Read Data.....................");
    	FlatFileItemReader<Product> read=new FlatFileItemReader<Product>();
    	//1.load file name + locatincation
    	  read.setResource(new ClassPathResource("product.csv"));
    	//2.read  data line by Line
    	  read.setLineMapper(new DefaultLineMapper<Product>() {{
         //3.tokenize data		
    		 setLineTokenizer(new DelimitedLineTokenizer() {{
    			 setDelimiter(DELIMITER_COMMA);
    			 setNames("prodId","prodName","prodcost");
    		 }}); 
    	 //4.convert into Object
    		 setFieldSetMapper(new  BeanWrapperFieldSetMapper<Product>() {{
    			 setTargetType(Product.class);
    		 }});
    	  }});
		return read;
	}
    
    //processor
    /*
   @Bean
   public ItemProcessor<Product, Product> process(){
	   return new MyItemProcessor();
   }*/
    
    @Bean
    public ItemProcessor<Product, Product> process(){
    	System.out.println("Item Processor Process The Data................");
 	   return (item)->{
           double cost = item.getProdcost();
           item.setProdGST(cost*12/100);
           item.setProdDiscount(cost*20/100);
          return item;
           };
    }
   
   //creating the ItemWriter object
    @Autowired
    private DataSource dataSource;
   @Bean
   public ItemWriter<Product> write(){
	   System.out.println("Item Writer Write The Data Into DB..................");
	   JdbcBatchItemWriter<Product> writer=new JdbcBatchItemWriter<Product>();
	   writer.setSql("INSERT INTO PRODUCT (PID,PNAME,PCOST ,PGST ,PDISCOUNT) VALUES(:prodId,:prodName,:prodcost,:prodDiscount,:prodGST)");
	   writer.setDataSource(dataSource);
	   writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
	   return writer;
   }
   
   //Listener
   /*
   @Bean
   public JobExecutionListener listener() {
	   return new MyJobExecutionListener();
   }
   */
   @Bean
   public JobExecutionListener listener() {
	   return new JobExecutionListener() {
		   
		   public void beforeJob(JobExecution je) {
				System.out.println("Before Starting Job Status ::> " + je.getStatus());
				System.out.println("Data And Time :: > " +new Date());
			}

			public void afterJob(JobExecution je) {
				System.out.println("After Completeting Job Status ::> " + je.getStatus());
				System.out.println(" Job End Time :: " + new Date());
				
			}
	   };
	     
			   }
   
   //applying @Autowired on StepBuilderFactory class
   @Autowired
   private StepBuilderFactory sf;
   
   //creating the Step Object
   @Bean
   public Step stepA() {
	 return sf.get("stepA")
			 .<Product,Product>chunk(20)
			 .reader(read())
			 .writer(write())
			 .processor(process())
			 .build();
   }
	
   //applying @Autowired on JobBuilderFactory
   @Autowired
   private JobBuilderFactory jf;
   
   //create Job object
   @Bean
   public Job jobA() {
	   return jf.get("jobA")
			   .incrementer(new RunIdIncrementer())
			   .listener(listener())
			   .start(stepA())
			   .build();
   }
}
