package com.day.on.api.exception

import com.day.on.common.exception.BusinessException
import com.day.on.common.response.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
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
            message = ex.message!!,
            errorCode = ex.errorCode,
            path = request.requestURI
        )
        
        return ResponseEntity
            .status(ex.httpStatus)
            .body(response)
    }
    
    /**
     * Spring 기본 예외들 처리 (예외 클래스명을 errorCode로 사용)
     */
    
    /**
     * @Valid 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        
        logger.warn("Validation failed at {}: {}", request.requestURI, ex.message)
        
        // 첫 번째 검증 실패 메시지 사용
        val errorMessage = ex.bindingResult.fieldErrors.firstOrNull()?.let {
            "${it.field}: ${it.defaultMessage}"
        } ?: "입력값이 올바르지 않습니다"
        
        val response = ErrorResponse.of(
            message = errorMessage,
            errorCode = ex.javaClass.simpleName,  // "MethodArgumentNotValidException"
            path = request.requestURI
        )
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response)
    }
    
    /**
     * 타입 변환 실패
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        
        logger.warn("Type mismatch at {}: {}", request.requestURI, ex.message)
        
        val paramName = ex.name
        val expectedType = ex.requiredType?.simpleName ?: "올바른 타입"
        val inputValue = ex.value
        val errorMessage = "파라미터 '${paramName}'는 ${expectedType}이어야 합니다. 입력값: '${inputValue}'"
        
        val response = ErrorResponse.of(
            message = errorMessage,
            errorCode = ex.javaClass.simpleName,  // "MethodArgumentTypeMismatchException"
            path = request.requestURI
        )
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response)
    }
    
    /**
     * 필수 파라미터 누락
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParameterException(
        ex: MissingServletRequestParameterException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        
        logger.warn("Missing parameter at {}: {}", request.requestURI, ex.message)
        
        val errorMessage = "필수 파라미터 '${ex.parameterName}'이(가) 누락되었습니다"
        
        val response = ErrorResponse.of(
            message = errorMessage,
            errorCode = ex.javaClass.simpleName,  // "MissingServletRequestParameterException"
            path = request.requestURI
        )
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response)
    }
    
    /**
     * HTTP 메서드 오류
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupportedException(
        ex: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        
        logger.warn("Method not supported at {}: {}", request.requestURI, ex.message)
        
        val supportedMethods = ex.supportedMethods?.joinToString(", ") ?: "Unknown"
        val errorMessage = "${ex.method} 메서드는 지원하지 않습니다. ${supportedMethods}를 사용해주세요"
        
        val response = ErrorResponse.of(
            message = errorMessage,
            errorCode = ex.javaClass.simpleName,  // "HttpRequestMethodNotSupportedException"
            path = request.requestURI
        )
        
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(response)
    }
    
    /**
     * Content-Type 오류
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleMediaTypeNotSupportedException(
        ex: HttpMediaTypeNotSupportedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        
        logger.warn("Media type not supported at {}: {}", request.requestURI, ex.message)
        
        val supportedTypes = ex.supportedMediaTypes.joinToString(", ")
        val errorMessage = "${ex.contentType}는 지원하지 않습니다. ${supportedTypes}을 사용해주세요"
        
        val response = ErrorResponse.of(
            message = errorMessage,
            errorCode = ex.javaClass.simpleName,  // "HttpMediaTypeNotSupportedException"
            path = request.requestURI
        )
        
        return ResponseEntity
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body(response)
    }
    
    /**
     * JSON 파싱 오류
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParseException(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        
        logger.warn("JSON parse error at {}: {}", request.requestURI, ex.message)
        
        val response = ErrorResponse.of(
            message = "JSON 형식이 올바르지 않습니다",
            errorCode = ex.javaClass.simpleName,  // "HttpMessageNotReadableException"
            path = request.requestURI
        )
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response)
    }
    
    /**
     * 파일 크기 초과
     */
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleFileSizeException(
        ex: MaxUploadSizeExceededException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        
        logger.warn("File size exceeded at {}: {}", request.requestURI, ex.message)
        
        val maxSize = ex.maxUploadSize / (1024 * 1024) // MB로 변환
        val errorMessage = "파일 크기가 ${maxSize}MB를 초과했습니다"
        
        val response = ErrorResponse.of(
            message = errorMessage,
            errorCode = ex.javaClass.simpleName,  // "MaxUploadSizeExceededException"
            path = request.requestURI
        )
        
        return ResponseEntity
            .status(HttpStatus.PAYLOAD_TOO_LARGE)
            .body(response)
    }
    
    /**
     * 데이터베이스 예외
     */
    @ExceptionHandler(DataAccessException::class)
    fun handleDatabaseException(
        ex: DataAccessException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        
        logger.error("Database error at {}: {}", request.requestURI, ex.message, ex)
        
        val response = ErrorResponse.of(
            message = "데이터 처리 중 오류가 발생했습니다",
            errorCode = ex.javaClass.simpleName,  // 구체적인 DB 예외 클래스명
            path = request.requestURI
        )
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response)
    }
    
    /**
     * 예상하지 못한 모든 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        
        logger.error("Unexpected error occurred at {}: {}", request.requestURI, ex.message, ex)
        
        val response = ErrorResponse.of(
            message = "서버 내부 오류가 발생했습니다",
            errorCode = ex.javaClass.simpleName,  // 실제 예외 클래스명
            path = request.requestURI
        )
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response)
    }
}
