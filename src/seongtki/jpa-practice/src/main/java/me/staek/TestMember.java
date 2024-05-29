package me.staek;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 */
@Entity
@Table(name = "Test_Member", uniqueConstraints = {@UniqueConstraint( name = "UNIQUE_TEST_MEMBER_NAME",
                                                columnNames = {"NAME"} )})
@SequenceGenerator(
        name = "SEQ_TEST_MEMBER_GEN",
        sequenceName = "SEQ_TEST_MEMBER",
        initialValue = 1, allocationSize = 50
)
//@TableGenerator(
//        name = "TEST_MEMBER_SEQ_GEN",
//        table = "SEQ_TEST_MEMBER",
//        pkColumnValue = "SEQ_TEST_MEMBER", allocationSize = 50)
public class TestMember {

    /**
     * GenerationType.IDENTITY
     * - Oracle 사용불가
     * - mysql 등 auto_increment 사용가능
     * - insert 위해서 persist 시점에 식별자를 먼저 DB로부터 받는다.
     *   - 50건 insert 시 sequence 50건 각각 요청해야 함.
     * GenerationType.SEQUENCE
     * - sequence에서 Increment 하는 값 만 큼 가져와서 사용할 수 있다.
     *   - Insert query 50건의 경우에, increament 가 50이면 sequence를 한 번만 조회해서 식별자 세팅 후 Insert한다.
     *
     * GenerationType.TABLE
     * - sequcene 하고 비슷한데 테이블을 로 값을 관리한다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TEST_MEMBER_GEN")
    @Column(name = "TEST_MEMBER_ID")
    private Long id;


    /**
     * unique : 잘 안씀 - 이름이 랜덤
     * default: insert 쿼리문에 name이 없어야 DB에서 자동으로 default value로 설정된다.
     * nullable : 런타임에 발생
     * length or varchar2(n) : 런타임에 발생
     */
    @Column(nullable = false, length = 50, columnDefinition = "varchar2(100) default 'Empty'")
//    @ColumnDefault("Default")
    private String name;

    @Column(insertable = true, updatable = true)
    private Long age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    /**
     * 일시 : date
     */
    private LocalDate testLocalDate;

    /**
     * 연월일시 : timestamp
     */
    private LocalDateTime testLocalDateTime;

    @Lob
    private String description;

    @Transient
    private String temp;

    public TestMember() {}

    public TestMember(String name) {
        this.name = name;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
