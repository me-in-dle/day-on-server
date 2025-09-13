package com.day.on

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@EnableFeignClients
@ConfigurationPropertiesScan
@EntityScan(basePackages = ["com"])
@ComponentScan(basePackages = ["com"])
@EnableJpaRepositories(basePackages = ["com"])
class DayOnApplication

fun main(args: Array<String>) {
    runApplication<DayOnApplication>(*args)
}
