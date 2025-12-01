package koval.proxyseller.twitter.integration

import koval.proxyseller.twitter.model.enumeration.Role
import koval.proxyseller.twitter.model.user.User
import koval.proxyseller.twitter.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired

class RepositoryIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    UserRepository userRepository

    def "should save and find user in MongoDB"() {
        given: "a new user"
        def user = new User()
        user.firstName = "Repository"
        user.lastName = "Test"
        user.username = "repotest"
        user.email = "repo.test@example.com"
        user.password = "hashedPassword"
        user.age = 25
        user.role = Role.ROLE_USER

        when: "saving the user"
        def savedUser = userRepository.save(user)

        then: "user should be saved"
        savedUser != null
        savedUser.id != null

        when: "finding the user by id"
        def foundUser = userRepository.findByIdAndIsDeletedFalse(savedUser.id)

        then: "user should be found"
        foundUser.isPresent()
        foundUser.get().username == "repotest"
        foundUser.get().email == "repo.test@example.com"
    }

    def "should find user by username"() {
        given: "a saved user"
        def user = new User()
        user.firstName = "Find"
        user.lastName = "ByUsername"
        user.username = "findbyusername"
        user.email = "find.byusername@example.com"
        user.password = "hashedPassword"
        user.age = 30
        user.role = Role.ROLE_USER
        userRepository.save(user)

        when: "finding user by username"
        def foundUser = userRepository.findByUsernameAndIsDeletedFalse("findbyusername")

        then: "user should be found"
        foundUser.isPresent()
        foundUser.get().username == "findbyusername"
    }

    def "should not find deleted user"() {
        given: "a saved and deleted user"
        def user = new User()
        user.firstName = "Deleted"
        user.lastName = "User"
        user.username = "deleteduser"
        user.email = "deleted.user@example.com"
        user.password = "hashedPassword"
        user.age = 25
        user.role = Role.ROLE_USER
        def savedUser = userRepository.save(user)
        savedUser.isDeleted = true
        userRepository.save(savedUser)

        when: "finding deleted user"
        def foundUser = userRepository.findByUsernameAndIsDeletedFalse("deleteduser")

        then: "user should not be found"
        foundUser.isEmpty()
    }
}

