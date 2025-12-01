package koval.proxyseller.twitter.exception

import koval.proxyseller.twitter.monitoring.MetricsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import spock.lang.Specification
import spock.lang.Subject

class GlobalExceptionHandlerSpec extends Specification {

    MetricsService metricsService = Mock()

    @Subject
    GlobalExceptionHandler handler = new GlobalExceptionHandler(metricsService)

    def "should handle EntityNotFoundException"() {
        given: "an EntityNotFoundException"
        def exception = new EntityNotFoundException("Entity not found")

        when: "handling the exception"
        def result = handler.handleEntityNotFoundException(exception)

        then: "should return 404 status"
        1 * metricsService.incrementEntityNotFoundErrors()
        result.statusCode == HttpStatus.NOT_FOUND
        result.body.error == "Entity Not Found"
    }

    def "should handle RegistrationException"() {
        given: "a RegistrationException"
        def exception = new RegistrationException("Email already exists")

        when: "handling the exception"
        def result = handler.handleRegistrationException(exception)

        then: "should return 409 status"
        result.statusCode == HttpStatus.CONFLICT
        result.body.error == "Registration Failed"
    }

    def "should handle DuplicateEntityException"() {
        given: "a DuplicateEntityException"
        def exception = new DuplicateEntityException("Duplicate entity")

        when: "handling the exception"
        def result = handler.handleDuplicateEntityException(exception)

        then: "should return 409 status"
        result.statusCode == HttpStatus.CONFLICT
        result.body.error == "Duplicate Entity"
    }

    def "should handle InvalidOperationException"() {
        given: "an InvalidOperationException"
        def exception = new InvalidOperationException("Invalid operation")

        when: "handling the exception"
        def result = handler.handleInvalidOperationException(exception)

        then: "should return 400 status"
        result.statusCode == HttpStatus.BAD_REQUEST
        result.body.error == "Invalid Operation"
    }

    def "should handle AuthenticationException"() {
        given: "an AuthenticationException"
        def exception = new AuthenticationException("Authentication failed")

        when: "handling the exception"
        def result = handler.handleAuthenticationException(exception)

        then: "should return 401 status"
        1 * metricsService.incrementAuthenticationErrors()
        result.statusCode == HttpStatus.UNAUTHORIZED
        result.body.error == "Authentication Failed"
    }

    def "should handle AuthorizationException"() {
        given: "an AuthorizationException"
        def exception = new AuthorizationException("Access denied")

        when: "handling the exception"
        def result = handler.handleAuthorizationException(exception)

        then: "should return 403 status"
        1 * metricsService.incrementAuthorizationErrors()
        result.statusCode == HttpStatus.FORBIDDEN
        result.body.error == "Access Denied"
    }

    def "should handle ResourceOwnershipException"() {
        given: "a ResourceOwnershipException"
        def exception = new ResourceOwnershipException("Not your resource")

        when: "handling the exception"
        def result = handler.handleResourceOwnershipException(exception)

        then: "should return 403 status"
        1 * metricsService.incrementAuthorizationErrors()
        result.statusCode == HttpStatus.FORBIDDEN
        result.body.error == "Resource Ownership Violation"
    }

    def "should handle ValidationException"() {
        given: "a ValidationException"
        def exception = new ValidationException("Validation failed")

        when: "handling the exception"
        def result = handler.handleValidationException(exception)

        then: "should return 400 status"
        1 * metricsService.incrementValidationErrors()
        result.statusCode == HttpStatus.BAD_REQUEST
        result.body.error == "Validation Failed"
    }

    def "should handle BadCredentialsException"() {
        given: "a BadCredentialsException"
        def exception = new BadCredentialsException("Bad credentials")

        when: "handling the exception"
        def result = handler.handleBadCredentialsException(exception)

        then: "should return 401 status"
        result.statusCode == HttpStatus.UNAUTHORIZED
        result.body.error == "Authentication Failed"
    }

    def "should handle AccessDeniedException"() {
        given: "an AccessDeniedException"
        def exception = new AccessDeniedException("Access denied")

        when: "handling the exception"
        def result = handler.handleAccessDeniedException(exception)

        then: "should return 403 status"
        result.statusCode == HttpStatus.FORBIDDEN
        result.body.error == "Access Denied"
    }

    def "should handle generic Exception"() {
        given: "a generic exception"
        def exception = new RuntimeException("Unexpected error")

        when: "handling the exception"
        def result = handler.handleGenericException(exception)

        then: "should return 500 status"
        result.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
        result.body.error == "Internal Server Error"
    }
}

