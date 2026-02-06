package com.auth.service.repository;

import com.auth.service.dto.UserDto;
import com.auth.service.model.User;
import com.auth.service.model.UserVerified;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

        boolean existsByEmail(String email);

        boolean existsByMobileNumber(String mobileNumber);

        @Query(value = "select (max(id) + 1) as user_id from user", nativeQuery = true)
        Integer fetchMaxUserId();

        @Query("SELECT u FROM User u  WHERE u.email = :email and u.isUserVerified=VERIFIED")
        Optional<User> findByEmail(String email);

        @Query("SELECT u FROM User u JOIN FETCH u.role r JOIN FETCH r.permissions WHERE u.email = :email")
        Optional<User> findByEmailWithRolesAndPermissions(@Param("email") String email);

        @Query("SELECT u FROM User u JOIN FETCH u.role r JOIN FETCH r.permissions WHERE u.mobileNumber = :mobileNumber")
        Optional<User> findByMobileWithRolesAndPermissions(@Param("mobileNumber") String mobileNumber);

        // @Query(value = "select * from user where mobile_number = :mobileNumber and
        // u.isUserVerified=VERIFIED order by id desc limit 1",
        // nativeQuery = true)
        // Optional<User> findByMobileNumber(@Param("mobileNumber") String
        // mobileNumber);
        @Query(value = "SELECT * FROM user WHERE mobile_number = :mobileNumber AND is_user_verified = 'VERIFIED' ORDER BY id DESC LIMIT 1", nativeQuery = true)
        Optional<User> findByMobileNumber(@Param("mobileNumber") String mobileNumber);

        @Query(value = "select password from user where id = :userId", nativeQuery = true)
        String findPassword(@Param("userId") Integer userId);

        @Query("select new com.auth.service.dto.UserDto(u.id, u.firstName, u.lastName, u.email, u.mobileNumber, u.status, u.isUserVerified) "
                        +
                        "from User u where u.createdBy = :id")
        List<UserDto> findUserDtosByCreatedBy(@Param("id") Integer id);

        Optional<User> findByIdAndCreatedBy(Integer childId, Integer parentId);

        @Query("select u from User u where u.id =:id and u.role.id =2")
        Optional<User> findByIdandRoleId(@Param("id") Integer id);

        @Query("select u from User u where u.id =:id and u.role.id =3")
        Optional<User> findDistributorByIdandRoleId(@Param("id") Integer id);

        @Query("select u from User u where u.id =:id and u.role.id =4")
        Optional<User> findRetailerByIdandRoleId(@Param("id") Integer id);

        @Query("select u from User u where u.role.id=2")
        List<User> findAllByRoleStockist();

        @Query("select u from User u where u.role.id=3")
        List<User> findAllByRoleDistributor();

        @Query("select u from User u where u.role.id=4")
        List<User> findAllByRoleRetailer();

        @Query("select u from User u where u.role.id=3 and u.id = :distributorId and u.createdBy=:stockistId")
        Optional<User> findAllByRoleDistributorByIdAndStockistId(@Param("distributorId") Integer distributorId,
                        @Param("stockistId") Integer stockistId);

        @Query("select u from User u where u.role.id=3 and u.createdBy=:stockistId")
        List<User> findAllByDistributorUnderStockist(@Param("stockistId") Integer stockistId);

        @Query("select u from User u where u.role.id=4 and u.createdBy=:distributorId")
        List<User> findAllRetailerUnderDistributor(Integer distributorId);

        @Query("select u from User u where u.role.id=4 and u.id = :retailerId and u.createdBy=:distributorId")
        Optional<User> findAllByRoleRetailerByIdAndDistributorId(Integer retailerId, Integer distributorId);

        @Query("SELECT us FROM User u JOIN User us ON u.createdBy = us.id WHERE u.id = :distributorId")
        Optional<User> findStockistByDistributor(@Param("distributorId") Integer distributorId);

        @Query("SELECT us FROM User u JOIN User us ON u.createdBy = us.id WHERE u.id = :retailerId")
        Optional<User> findDistributorByRetailer(Integer retailerId);

        @Query("SELECT u FROM User u " +
                        "WHERE u.role.id = :roleId " +
                        "AND u.state = :state " +
                        "AND u.status = 1 " +
                        "AND u.isUserVerified = com.auth.service.model.UserVerified.VERIFIED")
        List<User> findUsersByRoleIdAndState(@Param("roleId") int roleId, @Param("state") String state);

        @Query("SELECT u FROM User u " +
                        "WHERE u.role.id = :roleId " +
                        "AND u.status = 1 " +
                        "AND u.isUserVerified = VERIFIED")
        List<User> findUsersByRoleId(@Param("roleId") int roleId);

        @Query("SELECT DISTINCT new com.auth.service.dto.UserDto(u.id, u.firstName, u.lastName, u.email, u.mobileNumber, u.status, u.isUserVerified) "
                        +
                        "FROM User u " +
                        "WHERE u.role.id = :roleId AND u.state IN :states")
        List<UserDto> findUserDtosByRoleAndStates(@Param("roleId") int roleId, @Param("states") List<String> states);

        List<User> findByCreatedByAndIsUserVerified(Integer createdBy, UserVerified isUserVerified);

        @Query("select u from User u where u.role.id=4 and u.createdBy=:stockistId")
        List<User> findAllByRetailerUnderDistributor(@Param("stockistId") Integer distributorId);

        @Query("select u from User u where u.role.id=4 and u.id = :retailerId and u.createdBy=:distributorId")
        Optional<User> findAllByRoleRetailerIdAndDistributorId(@Param("retailerId") Integer retailerId,
                        @Param("distributorId") Integer distributorId);

        List<User> findByRoleRoleNameIgnoreCase(String roleName);

        List<User> findByRoleRoleNameIgnoreCaseAndCreatedBy(String roleName, Integer createdBy);

        @Query("select u from User u where u.id = :id and u.role.id=2 ")
        Optional<User> findByStockistId(Integer id);

        @Query("select u from User u where u.id = :id and u.role.id=3 ")
        Optional<User> findByDistributorId(Integer id);

        @Query("select u from User u where u.id = :id and u.role.id=4 ")
        Optional<User> findByRetailerId(Integer id);

        @Query("SELECT new com.auth.service.dto.UserDto(" +
                        "u.id, u.firstName, u.lastName, u.email, u.mobileNumber, u.status, u.isUserVerified, " +
                        "r.roleName, u.companyName, u.gst, u.city, u.state, u.address, u.reason) " +
                        "FROM User u LEFT JOIN u.role r")
        Page<UserDto> findAllUsers(Pageable pageable);

        @Query("SELECT new com.auth.service.dto.UserDto(u.id, u.firstName, u.lastName, u.email, " +
                        "u.mobileNumber, u.status, u.isUserVerified, r.roleName, u.companyName, u.gst, " +
                        "u.city, u.state, u.address) " +
                        "FROM User u JOIN u.role r " +
                        "WHERE (:roleId IS NULL OR r.id = :roleId) " +
                        "AND (:isUserVerified IS NULL OR u.isUserVerified = :isUserVerified)")
        List<UserDto> getAllUsersByStatus(@Param("roleId") Integer roleId,
                        @Param("isUserVerified") UserVerified isUserVerified);

        @Query(value = "SELECT * FROM user u " +
                        "WHERE (:mobileNumber IS NULL OR u.mobile_number = :mobileNumber) " +
                        "AND (:name IS NULL OR LOWER(u.first_name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                        "AND u.is_user_verified = 'VERIFIED' " +
                        "ORDER BY u.id DESC LIMIT 1", nativeQuery = true)
        Optional<User> findByMobileNumberAndName(@Param("mobileNumber") String mobileNumber,
                        @Param("name") String name);

        @Query("select u from User u where u.id = :distributorId and u.createdBy=:userId")
        Optional<User> findDistributorUnderStockist(Integer userId, Integer distributorId);

        @Query("select u from User u where u.id = :retailerId and u.createdBy=:userId")
        Optional<User> findRetailerUnderDistributor(Integer userId, Integer retailerId);

        @Query(value = "SELECT parent.*\n" +
                        "FROM user child\n" +
                        "JOIN user parent ON child.created_by = parent.id\n" +
                        "where child.id = :userId;", nativeQuery = true)
        User findParent(Integer userId);

        @Query("SELECT COUNT(u) FROM User u WHERE u.role.id = :roleId AND u.isUserVerified = :status")
        Integer countByRoleIdAndStatus(@Param("roleId") int roleId, @Param("status") UserVerified status);

        @Query("SELECT u.id FROM User u WHERE u.role.id = :roleId AND u.isUserVerified = :status")
        List<Integer> findUserIdsByRoleIdAndStatus(@Param("roleId") int roleId, @Param("status") UserVerified status);

        @Query("SELECT u.id, u.firstName, u.email, u.role.roleName, u.isUserVerified, u.createdAt, u.city, u.state, u.status FROM User u WHERE u.createdAt >= :start ORDER BY u.createdAt DESC")
        List<Object[]> findRecentUsers(@Param("start") LocalDateTime start, Pageable pageable);

        @Query("select u from User u where u.role.id = 2 and u.state=:state")
        List<User> findStockistByState(@Param("state") String state);

        Integer countByCreatedBy(Integer createdBy);

        @Query("SELECT DISTINCT a.state FROM User a WHERE a.state IS NOT NULL")
        Page<String> findAllDistinctStates(Pageable pageable);

        // List<String> findStatesByRoleId(int roleId);

        @Query("SELECT DISTINCT u.state FROM User u WHERE u.role.id = :roleId AND u.state IS NOT NULL")
        List<String> findStatesByRoleId(@Param("roleId") int roleId);

        @Query("SELECT new com.auth.service.dto.UserDto(" +
                        "u.id, u.firstName, u.lastName, u.email, u.mobileNumber, " +
                        "u.status, u.isUserVerified, u.role.roleName) " +
                        "FROM User u WHERE u.role.roleName = :roleName")
        Page<UserDto> findUserDtosByRoleName(@Param("roleName") String roleName, Pageable pageable);

        @Query("SELECT new com.auth.service.dto.UserDto(" +
                        "u.id, u.firstName, u.lastName, u.email, u.mobileNumber, " +
                        "u.status, u.isUserVerified, u.role.roleName) " +
                        "FROM User u WHERE u.createdBy = :createdBy")
        Page<UserDto> findUserDtosByCreatedBy(@Param("createdBy") Integer createdBy, Pageable pageable);

        @Query("SELECT new com.auth.service.dto.UserDto(" +
                        "u.id, u.firstName, u.lastName, u.email, u.mobileNumber, " +
                        "u.status, u.isUserVerified, u.role.roleName) " +
                        "FROM User u WHERE u.id = :id")
        Optional<UserDto> findUserDtoById(@Param("id") Integer id);

        @Query("SELECT new com.auth.service.dto.UserDto(u.id, u.firstName, u.lastName, u.email, u.mobileNumber, u.status, u.isUserVerified, r.roleName, u.companyName, u.gst, u.city, u.state, u.address) "
                        +
                        "FROM User u JOIN u.role r " +
                        "WHERE (:userId IS NULL OR u.createdBy = :userId) " +
                        "AND (:isUserVerified IS NULL OR u.isUserVerified = :isUserVerified)")
        List<UserDto> getAllRejectedUsersByCreatedBy(@Param("userId") Integer userId,
                        @Param("isUserVerified") UserVerified isUserVerified);

        @Query(value = "SELECT * FROM user u WHERE u.id = :userId", nativeQuery = true)
        User findByUserId(@Param("userId") Integer userId);

        @Query("select u from User u  where u.role.id !=1")
        List<User> findAllUser();

        @Query("SELECT u FROM User u WHERE u.createdBy = :createdBy")
        Page<User> findAllByCreatedBy(@Param("createdBy") Integer createdBy, Pageable pageable);

        @Query(value = "select * from user where email_id = :email", nativeQuery = true)
        List<User> findUserByEmail(String email);

        @Query(value = "select * from user where mobile_number = :mobileNumber", nativeQuery = true)
        List<User> findUserByMobileNumber(String mobileNumber);

        @Query("SELECT u.id FROM User u WHERE u.role.id = 2")
        List<Integer> findAllStockistIds();

        @Query("SELECT u.id FROM User u WHERE u.role.id = 3")
        List<Integer> findAllDistributorIds();

        @Query("SELECT u.id FROM User u WHERE u.role.id = 3 AND u.createdBy = :stockistId")
        List<Integer> findDistributorsByStockistId(@Param("stockistId") Integer stockistId);

        @Query("SELECT u.id FROM User u WHERE u.role.id = 4 and  u.createdBy=:distributorId")
        List<Integer> findAllRetailerIds(@Param("distributorId") Integer distributorId);

        @Query("SELECT u.id FROM User u WHERE u.role.id = 4")
        List<Integer> findAllRetailerIds();

        @Query("SELECT u.id FROM User u WHERE u.role.id = 4 AND u.createdBy = :distributorId")
        List<Integer> findRetailersByDistributorId(@Param("distributorId") Integer distributorId);

        @Query(value = "SELECT * FROM user WHERE first_name LIKE %:query% AND mobile_number LIKE %:mobileNumber% AND status = 1", nativeQuery = true)
        List<User> searchByUserNameAndMobile(@Param("query") String query, @Param("mobileNumber") String mobileNumber);

        List<User> findByRole_RoleName(String roleName);

        Optional<User> findFirstByRole_Id(Integer roleId);

        @Query("SELECT u.id FROM User u WHERE u.createdBy = :stockistId AND u.role.roleName = 'DISTRIBUTOR'")
        List<Integer> findDistributorIdsUnderStockist(@Param("stockistId") Integer stockistId);

        @Query("SELECT u.id FROM User u WHERE u.createdBy = :distributorId AND u.role.roleName = 'RETAILER'")
        List<Integer> findRetailerIdsUnderDistributor(@Param("distributorId") Integer distributorId);

        List<User> findByCreatedBy(Integer createdBy);

        // @Query("SELECT u.id FROM User u WHERE u.createdBy = :createdBy AND u.role.id
        // = :roleId")
        // List<Integer> findIdsByCreatedByAndRoleId(@Param("createdBy") Integer
        // createdBy,
        // @Param("roleId") Integer roleId);

        // @Query("SELECT u.id FROM User u WHERE u.createdBy = :createdBy AND u.role.id
        // = :roleId")
        // List<Integer> findIdsByCreatedByAndRoleId(@Param("createdBy") Integer
        // createdBy, @Param("roleId") Integer roleId);

        @Query("SELECT u.id FROM User u WHERE u.createdBy = :createdBy AND u.role.id = :roleId AND u.status = 1")
        List<Integer> findIdsByCreatedByAndRoleId(@Param("createdBy") Integer createdBy,
                        @Param("roleId") Integer roleId);

        Page<User> findByRole_RoleNameAndStatusAndIsUserVerified(String roleName, Integer status,
                        UserVerified isUserVerified, Pageable pageable);
}
