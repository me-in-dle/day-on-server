package com.day.on.calendar.client

import feign.codec.Encoder
import feign.form.spring.SpringFormEncoder
import org.springframework.beans.factory.ObjectFactory
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.SpringEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class FeignFormConfig {

    /**
     * 이 빈을 주입해서 SpringFormEncoder를 생성
     */
    @Bean
    fun feignFormEncoder(objectFactory: ObjectFactory<HttpMessageConverters>): Encoder {
        // SpringFormEncoder를 SpringEncoder로 래핑
        return SpringFormEncoder(SpringEncoder(objectFactory))
    }
}