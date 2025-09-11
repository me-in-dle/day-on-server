package com.day.on.common.lock

import com.day.on.common.outbound.LockManager
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.annotation.Order
import org.springframework.expression.EvaluationContext
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@Order(1)
@Component
@Aspect
class DistributedLockAspect(
    private val distributedLockManager: LockManager,
    private val transactionManager: PlatformTransactionManager,
) {
    @Around("@annotation(distributedLockAnnotation)")
    fun round(
        joinPoint: ProceedingJoinPoint,
        distributedLockAnnotation: DistributedLockAnnotation,
    ): Any? {
        val lockKey =
            with(distributedLockAnnotation) {
                lockKey(
                    joinPoint = joinPoint,
                    key = key,
                    prefix = prefix.name,
                    separator = separator,
                )
            }
        return distributedLockManager.lock(lockKey) {
            joinPoint.proceed()
        }
    }

    @Around("@annotation(distributedLockBeforeTransactionAnnotation)")
    fun around(
        joinPoint: ProceedingJoinPoint,
        distributedLockBeforeTransactionAnnotation: DistributedLockBeforeTransactionAnnotation,
    ): Any? {
        val lockKey =
            with(distributedLockBeforeTransactionAnnotation) {
                lockKey(
                    joinPoint = joinPoint,
                    key = key,
                    prefix = prefix.name,
                    separator = separator,
                )
            }
        return distributedLockManager.lock(lockKey) {
            val transactionTemplate =
                TransactionTemplate(transactionManager).apply {
                    isReadOnly = distributedLockBeforeTransactionAnnotation.transactionalReadOnly
                }
            transactionTemplate.execute {
                joinPoint.proceed()
            }
        }
    }

    private fun lockKey(
        joinPoint: ProceedingJoinPoint,
        key: Array<String>,
        prefix: String,
        separator: String,
    ): String {
        val evaluationContext = createEvaluationContext(joinPoint)
        return key.asSequence()
            .map { EXPRESSION_PARSER.parseExpression(it) }
            .map {
                requireNotNull(it.getValue(evaluationContext)) {
                    "DistributedLock Key가 잘못되었습니다."
                }
            }
            .joinToString(
                prefix = "${prefix}$separator",
                separator = separator,
            )
    }

    private fun createEvaluationContext(joinPoint: ProceedingJoinPoint): EvaluationContext {
        val parameters = joinPoint.parameters()
        return StandardEvaluationContext().apply { setVariables(parameters) }
    }

    private fun JoinPoint.parameters() =
        (signature as MethodSignature).parameterNames.asSequence().zip(args.asSequence()).toMap()

    companion object {
        private val EXPRESSION_PARSER: ExpressionParser = SpelExpressionParser()
    }
}