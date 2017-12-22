package de.codecentric.opentracing.instana.demo.notebackend;

import de.codecentric.opentracing.instana.demo.notebackend.persistence.NoteEntity;
import de.codecentric.opentracing.instana.demo.notebackend.persistence.NoteRepo;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class NoteBackendApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(NoteBackendApplication.class);

    @Autowired
    private NoteRepo noteRepo;

    @Bean
    public Sampler sampler() {
        return new AlwaysSampler();
    }

    public static void main(String[] args) {
        SpringApplication.run(NoteBackendApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        Stream.of(
                new NoteEntity("Note 1"),
                new NoteEntity("Note 2"),
                new NoteEntity("Note 3")
            )
            .forEach(noteRepo::save);

        log.info("Number of added notes: " + noteRepo.count());

    }
}
