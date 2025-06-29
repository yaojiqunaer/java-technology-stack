package io.github.yaojiqunaer.batch.config;

import io.github.yaojiqunaer.batch.model.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Spring Batch 配置类
 */
@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names("firstName", "lastName", "age", "email")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    public ItemProcessor<Person, String> processor() {
        return person -> person.getFirstName().toUpperCase() + " " + 
                         person.getLastName().toUpperCase() + ", Age: " + 
                         person.getAge() + ", Email: " + person.getEmail();
    }

    @Bean
    public FlatFileItemWriter<String> writer() {
        return new FlatFileItemWriterBuilder<String>()
                .name("personItemWriter")
                .resource(new FileSystemResource("target/processed-persons.txt"))
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
    }

    @Bean
    public Job importPersonJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importPersonJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      ItemReader<Person> reader,
                      ItemProcessor<Person, String> processor,
                      ItemWriter<String> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Person, String>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
} 