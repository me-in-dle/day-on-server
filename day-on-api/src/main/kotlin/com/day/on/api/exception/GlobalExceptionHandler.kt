package com.day.on.api.exception

import com.day.on.account.exception.AccountErrorCode
import com.day.on.account.exception.AccountException
import com.day.on.api.response.ErrorResponse
import com.day.on.common.exception.BusinessException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MaxUploadSizeExceededException

/**
 * 전역 예외 처리기
 * 애플리케이션에서 발생하는 모든 예외를 통일된 ErrorResponse 형식으로 처리합니다.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        ex: BusinessException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        logger.warn("Business exception occurred at {}: {}", request.requestURI, ex.message, ex)

        val response = ErrorResponse.of(
            message = ex.message,
            errorCode = ex.errorCode,
            path = request.requestURI
        )

        val status = when (ex) {
            is AccountException -> {
                val exceptionCode = ex.errorCode as AccountErrorCode
                when (exceptionCode) {
                    AccountErrorCode.ACCOUNT_NOT_FOUND -> HttpStatus.NOT_FOUND

                    AccountErrorCode.ACCOUNT_ALREADY_EXISTS, AccountErrorCode.INVALID_EMAIL_FORMAT,
                    AccountErrorCode.EMAIL_ALREADY_EXISTS, AccountErrorCode.INVALID_PASSWORD -> HttpStatus.BAD_REQUEST

                    AccountErrorCode.TOKEN_EXPIRED, AccountErrorCode.INVALID_TOKEN,
                    AccountErrorCode.ACCOUNT_DISABLED -> HttpStatus.UNAUTHORIZED
                }
            }

            else -> HttpStatus.BAD_REQUEST
        }

        return ResponseEntity
            .status(status)
            .body(response)
    }


    /**
     * Spring 기본 예외들 처리 (예외 클래스명을 errorCode로 사용)
     */
    @ExceptionHandler(Exception::class)
    fun handleValidationException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        logger.warn("handle failed at {}: {}", request.requestURI, ex.message)

        val errorMessage = when (ex) {
            is MethodArgumentNotValidException -> {
                ex.bindingResult.fieldErrors.firstOrNull()?.let {
                    "${it.field}: ${it.defaultMessage}"
                } ?: "입력값이 올바르지 않습니다"
            }

            is MethodArgumentTypeMismatchException -> {
                "파라미터 '${ex.name}'는 ${ex.requiredType?.simpleName ?: "올바른 타입"}이어야 합니다. 입력값: '${ex.value}'"
            }

            is MissingServletRequestParameterException -> {
                "필수 파라미터 '${ex.parameterName}'이(가) 누락되었습니다"
            }

            is HttpRequestMethodNotSupportedException -> {
                val supportedMethods = ex.supportedMethods?.joinToString(", ") ?: "Unknown"
                "${ex.method} 메서드는 지원하지 않습니다. ${supportedMethods}를 사용해주세요"
            }

            is HttpMediaTypeNotSupportedException -> {
                val supportedTypes = ex.supportedMediaTypes.joinToString(", ")
                "${ex.contentType}는 지원하지 않습니다. ${supportedTypes}을 사용해주세요"
            }

            is HttpMessageNotReadableException -> {
                "JSON 형식이 올바르지 않습니다"
            }

            is MaxUploadSizeExceededException -> {
                val maxSize = ex.maxUploadSize / (1024 * 1024) // MB로 변환
                "파일 크기가 ${maxSize}MB를 초과했습니다"
            }

            else -> {
                logger.error("Unhandled exception at {}: {}", request.requestURI, ex.message, ex)
                "서버 내부 오류가 발생했습니다"
            }
        }

        val response = ErrorResponse.of(
            message = errorMessage,
            ex = ex,
            path = request.requestURI
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response)
    }
}
