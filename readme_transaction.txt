Transactions Related Problems
-----------------------------
Transaction example is inside SampleSpringBootDataJpaProject

RDBMS provides ACID properties (atomicity, consistency, integrity, durability)
Transaction management is required for Atomicity.


https://en.wikipedia.org/wiki/Isolation_(database_systems)

Read only Transaction
    If you say @Transactional(readonly=true) it means that it will be jdbc's connection.setReadOnly(true). That transaction is allowed to do only reads and no writes.

Propagation Levels

    REQUIRES_NEW - Create a new transaction, and suspend the current transaction if one exists.
    REQUIRED - Support a current transaction, create a new one if none exists.
    SUPPORTS - Support a current transaction, execute non-transactionally if none exists.
    NOT_SUPPORTED - Execute non-transactionally, suspend the current transaction if one exists.
    MANDATORY - Support a current transaction, throw an exception if none exists.
    NESTED - Execute within a nested transaction if a current transaction exists, behave like PROPAGATION_REQUIRED else.
    NEVER - Execute non-transactionally, throw an exception if a transaction exists.

Isolation Levels

    Read Uncommitted
    Read Committed -- saves you from Dirty Reads
    Repeatable Read -- saves you from Dirty + Non-repeatable reads
    Serializable -- saves you from Dirty + Non-repeatable + Phantom reads

Read phenomena

    Dirty reads -

        Tx1 - SELECT age FROM users WHERE id = 1;

                                        Tx2 - UPDATE users SET age = 21 WHERE id = 1;

        Tx1 - SELECT age FROM users WHERE id = 1; --- This will read age=21
                                        Tx2 - rollback;

        A dirty read (aka uncommitted dependency) occurs when a tx1 is allowed to read data from a row that has been modified by another running transaction (tx2) and not yet committed.
        No need to lock the row where id=1. Simply, setting isolation_level=READ_COMMITTED will prevent tx1 from reading uncommited changes from tx2.

    Repeatable reads -

        Let's say you applied isolation_level=READ_COMMITTED, but what if before second read by TX1, TX2 commits the changes to a row.
        In this case, tx2 will read changed values, if it reads the row again.

        Tx1 - SELECT * FROM users WHERE id = 1;

                                        Tx2 - UPDATE users SET age = 21 WHERE id = 1;
                                              COMMIT;

        Tx1 - SELECT * FROM users WHERE id = 1; --- This will read age=21

        To prevent this, you need to apply isolation_level=READ_REPEATABLE, which will lock the row with id=1 when TX1 reads it first time.
        It won't release the lock until TX1 commits or rollbacks. This will prevent Tx2 to update/delete the row. Tx2 has to wait until Tx1 is finished.

    Phantom reads -

        Let's say you applied isolation_level=READ_REPEATABLE, but what if tx2 inserts new data for the same range that tx1 has read data.

        Tx1 - SELECT * FROM users
              WHERE age BETWEEN 10 AND 30;
                                    Tx2 - INSERT INTO users(id,name,age) VALUES ( 3, 'Bob', 27 );
                                          COMMIT;
        Tx1 - SELECT * FROM users
              WHERE age BETWEEN 10 AND 30;
              COMMIT;

        isolation_level=READ_REPEATABLE will lock the rows where age BETWEEN 10 AND 30, but it doesn't stop tx2 to insert rows in the same range.
        To prevent this, you need to apply isolation_level=SERIALIZABLE. This will apply Range level lock. It will lock a range of age BETWEEN 10 AND 30.
        So, Tx2 won't be allowed to insert or update any records in the same range.

    isolation_level=SERIALIZABLE is the most expensive. It can degrade the performance of your application.

    In above cases, Tx2 might timeout while waiting for lock to be released acquired by Tx1. You can set this timeout period in @Transactional.


Two-Phase Commit (2PC)
----------------------
http://stackoverflow.com/questions/7389382/two-phase-commit

Basically, it is used to make sure the transactions are in sync when you have 2 or more DBs.

Assume I've two DBs (A and B) using 2PC in two different locations. Before A and B are ready to commit a transaction, both DBs will report back to the transaction manager saying they are ready to commit. So, when the transaction manager is acknowledged, it will send a signal back to A and B telling them to go ahead.

Spring Transaction Management
-----------------------------

http://docs.spring.io/spring/docs/current/spring-framework-reference/html/transaction.html

Programmatic approach to execute a transaction
----------------------------------------------
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="${jdbc.driverClassName}" />
    <property name="url" value="${jdbc.url}" />
    <property name="username" value="${jdbc.username}" />
    <property name="password" value="${jdbc.password}" />
</bean>
<bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>

If you use JTA in a Java EE container then you use a container DataSource, obtained through JNDI, in conjunction with Spring’s JtaTransactionManager.
 <jee:jndi-lookup id="dataSource" jndi-name="jdbc/jpetstore"/>

 <bean id="txManager" class="org.springframework.transaction.jta.JtaTransactionManager" />

The JtaTransactionManager does not need to know about the DataSource, or any other specific resources, because it uses the container’s global transaction management infrastructure.
If you are using Hibernate and Java EE container-managed JTA transactions, then you should simply use the same JtaTransactionManager as in the previous JTA example for JDBC.



<bean id="transactionTemplate"
        class="org.springframework.transaction.support.TransactionTemplate">
    <property name="transactionManager" ref="txManager:/>
    <property name="isolationLevelName" value="ISOLATION_READ_UNCOMMITTED"/>
    <property name="timeout" value="30"/>
</bean>"


transactionTemplate.execute(new TransactionCallbackWithoutResult() {
    protected void doInTransactionWithoutResult(TransactionStatus status) {
        updateOperation1();
        updateOperation2();
    }
});

transactionTemplate.execute(new TransactionCallbackWithoutResult() {

    protected void doInTransactionWithoutResult(TransactionStatus status) {
        try {
            updateOperation1();
            updateOperation2();
        } catch (SomeBusinessExeption ex) {
            status.setRollbackOnly();
        }
    }
});


TransactionTemplate extends TransactionDefinition
 contains propagation, isolation readonly, timeout, name etc information

When transactionTemplate.execute(TransactionCallback) is called

execute(TransactionCallback action) method -
	1) TransactionStatus status = transactionManager.getTransaction(transactionDefinition) --- based on set propagation levels, it either gives the currently transaction back or gives a new transaction or suspends current transaction and gives a new one or throws an exception etc. If new transaction has to be created then it uses doBegin() method that uses ConnectionHolder to get the connection from set datasource.

	2) result = action.doInTransaction(status);
		try {
				result = action.doInTransaction(status);
		}
		catch(Any exception) {
			rollbackOnException() --- if there was a new transaction created, then it simply does connection.rollback(), if existing transaction was being used then sets Transaction status as rollBackOnly
		}
		transactionManager.commit() --- do actual commit

		Based on whatever has been done with Transaction, it populates it status in TransactionStatus.


Before and after rollback/commit, many TransactionSynchronization callbacks are executed.

TransactionSynchronization
	If something needs to be done before and/or after transaction is rollbacked/committed, then you can create custom TransactionSynchronization and write your own code that needs to be executed beforeCommit, beforeCompletion, afterCommit, afterComplettion et


TransactionStatus has
	boolean isNewTransaction();
	boolean hasSavepoint();
	void setRollbackOnly(); --- during any point of time of executing a transaction, if there is an exception, then it sets transaction status as RollbackOnly.  This instructs the transaction manager that the only possible outcome of the transaction may be a rollback, as alternative to throwing an exception which would in turn trigger a rollback.


Declarative Approach
--------------------
http://docs.spring.io/spring/docs/current/spring-framework-reference/html/transaction.html

The most important concepts to grasp with regard to the Spring Framework’s declarative transaction support are that this support is enabled via AOP proxies

package x.y.service;

public class DefaultFooService implements FooService {

    public Foo getFoo(String fooName) {
        throw new UnsupportedOperationException();
    }

    public Foo getFoo(String fooName, String barName) {
        throw new UnsupportedOperationException();
    }

    public void insertFoo(Foo foo) {
        throw new UnsupportedOperationException();
    }

    public void updateFoo(Foo foo) {
        throw new UnsupportedOperationException();
    }

}
<!-- this is the service object that we want to make transactional -->
    <bean id="fooService" class="x.y.service.DefaultFooService"/>

    <!-- the transactional advice (what 'happens'; see the <aop:advisor/> bean below) -->
    <tx:advice id="txAdvice" transaction-manager="txManager">
        <!-- the transactional semantics... -->
        <tx:attributes>
            <!-- all methods starting with 'get' are read-only -->
            <tx:method name="get*" read-only="true"/>
            <!-- other methods use the default transaction settings (see below) -->
            <tx:method name="*"/>
        </tx:attributes>
    </tx:advice>

    <!-- ensure that the above transactional advice runs for any execution
        of an operation defined by the FooService interface -->
    <aop:config>
        <aop:pointcut id="fooServiceOperation" expression="execution(* x.y.service.FooService.*(..))"/>
        <aop:advisor advice-ref="txAdvice" pointcut-ref="fooServiceOperation"/>
    </aop:config>

    <!-- don't forget the DataSource -->
    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="oracle.jdbc.driver.OracleDriver"/>
        <property name="url" value="jdbc:oracle:thin:@rj-t42:1521:elvis"/>
        <property name="username" value="scott"/>
        <property name="password" value="tiger"/>
    </bean>

    <!-- similarly, don't forget the PlatformTransactionManager -->
    <bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>



Spring AOP uses proxying:
Spring AOP uses either JDK dynamic proxies or CGLIB to create the proxy for a given target object.
(JDK dynamic proxies are preferred whenever you have a choice).
If the target object to be proxied implements at least one interface then a JDK dynamic proxy will be
used. All of the interfaces implemented by the target type will be proxied. If the target object does not
implement any interfaces then a CGLIB proxy will be created.
If you want to force the use of CGLIB proxying (for example, to proxy every method defined for the
target object, not just those implemented by its interfaces) you can do so.



The output from running the preceding program will resemble the following.

<!-- the Spring container is starting up... -->
[AspectJInvocationContextExposingAdvisorAutoProxyCreator] - Creating implicit proxy for bean 'fooService' with 0 common interceptors and 1 specific interceptors

<!-- the DefaultFooService is actually proxied -->
[JdkDynamicAopProxy] - Creating JDK dynamic proxy for [x.y.service.DefaultFooService]

<!-- ... the insertFoo(..) method is now being invoked on the proxy -->
[TransactionInterceptor] - Getting transaction for x.y.service.FooService.insertFoo

<!-- the transactional advice kicks in here... -->
[DataSourceTransactionManager] - Creating new transaction with name [x.y.service.FooService.insertFoo]
[DataSourceTransactionManager] - Acquired Connection [org.apache.commons.dbcp.PoolableConnection@a53de4] for JDBC transaction

<!-- the insertFoo(..) method from DefaultFooService throws an exception... -->
[RuleBasedTransactionAttribute] - Applying myRules to determine whether transaction should rollback on java.lang.UnsupportedOperationException
[TransactionInterceptor] - Invoking rollback for transaction on x.y.service.FooService.insertFoo due to throwable [java.lang.UnsupportedOperationException]

<!-- and the transaction is rolled back (by default, RuntimeException instances cause rollback) -->
[DataSourceTransactionManager] - Rolling back JDBC transaction on Connection [org.apache.commons.dbcp.PoolableConnection@a53de4]
[DataSourceTransactionManager] - Releasing JDBC Connection after transaction
[DataSourceUtils] - Returning JDBC Connection to DataSource

Exception in thread "main" java.lang.UnsupportedOperationException at x.y.service.DefaultFooService.insertFoo(DefaultFooService.java:14)
<!-- AOP infrastructure stack trace elements removed for clarity -->
at $Proxy0.insertFoo(Unknown Source)




TransactionInterceptor:-
 It is actually a MethodInterceptor with invoke() method. When any transacted method is invoked, a proxy TransactionInterceptor is used to create an around advise
 for that method call. Before calling target class method, this interceptor actually creates transaction first from all information (Transaction Definition) provided for that method.

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		// Work out the target class: may be {@code null}.
		// The TransactionAttributeSource should be passed the target class
		// as well as the method, which may be from an interface.
		Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

		// Adapt to TransactionAspectSupport's invokeWithinTransaction...
		return invokeWithinTransaction(invocation.getMethod(), targetClass, new InvocationCallback() {
			@Override
			public Object proceedWithInvocation() throws Throwable {
				return invocation.proceed();
			}
		});
	}

	Object invokeWithinTransaction(Method method, Class<?> targetClass, final InvocationCallback invocation) {
		final TransactionAttribute txAttr = getTransactionAttributeSource().getTransactionAttribute(method, targetClass);
		final PlatformTransactionManager tm = determineTransactionManager(txAttr);
		final String joinpointIdentification = methodIdentification(method, targetClass);

		if (txAttr == null || !(tm instanceof CallbackPreferringPlatformTransactionManager)) {
			// Standard transaction demarcation with getTransaction and commit/rollback calls.
			TransactionInfo txInfo = createTransactionIfNecessary(tm, txAttr, joinpointIdentification); --- creating transaction info like readonly/propogation/isolation etc
			Object retVal = null;
			try {
				// This is an around advice: Invoke the next interceptor in the chain.
				// This will normally result in a target object being invoked.
				retVal = invocation.proceedWithInvocation();   ----------- actual method invocation
			}
			catch (Throwable ex) {
				// target invocation exception
				completeTransactionAfterThrowing(txInfo, ex); --- rolling back transaction
				throw ex;
			}
			finally {
				cleanupTransactionInfo(txInfo);
			}
			commitTransactionAfterReturning(txInfo); ---- commiting transaction
			return retVal;
		}

	}

@Transactional
--------------
@Transactional
public class DefaultFooService implements FooService {
...
}

You can place the @Transactional annotation before an interface definition, a method on an interface, a class definition, or a public method on a class.

Spring recommends that you only annotate concrete classes (and methods of concrete classes) with the @Transactional annotation, as opposed to annotating interfaces. You certainly can place the @Transactional annotation on an interface (or an interface method), but this works only as you would expect it to if you are using interface-based proxies. The fact that Java annotations are not inherited from interfaces means that if you are using class-based proxies ( proxy-target-class="true") or the weaving-based aspect ( mode="aspectj"), then the transaction settings are not recognized by the proxying and weaving infrastructure, and the object will not be wrapped in a transactional proxy, which would be decidedly bad.


<!-- enable the configuration of transactional behavior based on annotations -->
<tx:annotation-driven transaction-manager="txManager"/>
or
use @EnableTransactionManagement


@Transactional(readOnly = true)
public class DefaultFooService implements FooService {

    public Foo getFoo(String fooName) {
        // do something
    }

    // these settings have precedence for this method
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updateFoo(Foo foo) {
        // do something
    }
}

@Transactional settings:-

The @Transactional annotation is metadata that specifies that an interface, class, or method must have transactional semantics; for example, "start a brand new read-only transaction when this method is invoked, suspending any existing transaction". The default @Transactional settings are as follows:

	Propagation setting is PROPAGATION_REQUIRED.
	Isolation level is ISOLATION_DEFAULT.
	Transaction is read/write.
	Transaction timeout defaults to the default timeout of the underlying transaction system, or to none if timeouts are not supported.
	Any RuntimeException triggers rollback, and any checked Exception does not.


public class TransactionalService {

    @Transactional("order") --- transaction manager name
    public void setSomething(String name) { ... }

    @Transactional("account")
    public void doSomething() { ... }
}

<bean id="transactionManager1" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        ...
        <qualifier value="order"/>
    </bean>

    <bean id="transactionManager2" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        ...
        <qualifier value="account"/>
    </bean>

You can read more about 'Transaction propagation' and 'Transaction bound event' from "http://docs.spring.io/spring/docs/current/spring-framework-reference/html/transaction.html"


@EnableTransactionManagement:-

This text is taken from this annotation class.

&#064;Configuration
 * &#064;EnableTransactionManagement
 * public class AppConfig {
 *     &#064;Bean
 *     public FooRepository fooRepository() {
 *         // configure and return a class having &#064;Transactional methods
 *         return new JdbcFooRepository(dataSource());
 *     }
 *
 *     &#064;Bean
 *     public DataSource dataSource() {
 *         // configure and return the necessary JDBC DataSource
 *     }
 *
 *     &#064;Bean
 *     public PlatformTransactionManager txManager() {
 *         return new DataSourceTransactionManager(dataSource());
 *     }
 * }

<beans>
 *     <tx:annotation-driven/>
 *     <bean id="fooRepository" class="com.foo.JdbcFooRepository">
 *         <constructor-arg ref="dataSource"/>
 *     </bean>
 *     <bean id="dataSource" class="com.vendor.VendorDataSource"/>
 *     <bean id="transactionManager" class="org.sfwk...DataSourceTransactionManager">
 *         <constructor-arg ref="dataSource"/>
 *     </bean>
 * </beans>


In both of the scenarios above, {@code @EnableTransactionManagement} and {@code
 * <tx:annotation-driven/>} are responsible for registering the necessary Spring
 * components that power annotation-driven transaction management, such as the
 * TransactionInterceptor and the proxy- or AspectJ-based advice that weave the
 * interceptor into the call stack when {@code JdbcFooRepository}'s {@code @Transactional}
 * methods are invoked.
 *
 * A minor difference between the two examples lies in the naming of the {@code
 * PlatformTransactionManager} bean: In the {@code @Bean} case, the name is
 * <em>"txManager"</em> (per the name of the method); in the XML case, the name is
 * <em>"transactionManager"</em>. The {@code <tx:annotation-driven/>} is hard-wired to
 * look for a bean named "transactionManager" by default, however
 * {@code @EnableTransactionManagement} is more flexible; it will fall back to a by-type
 * lookup for any {@code PlatformTransactionManager} bean in the container. Thus the name
 * can be "txManager", "transactionManager", or "tm": it simply does not matter.
 *
 * For those that wish to establish a more direct relationship between
 * {@code @EnableTransactionManagement} and the exact transaction manager bean to be used,
 * the {@link TransactionManagementConfigurer} callback interface may be implemented -
 * notice the {@code implements} clause and the {@code @Override}-annotated method below:


 * &#064;Configuration
 * &#064;EnableTransactionManagement
 * public class AppConfig implements TransactionManagementConfigurer {
 *     &#064;Bean
 *     public FooRepository fooRepository() {
 *         // configure and return a class having &#064;Transactional methods
 *         return new JdbcFooRepository(dataSource());
 *     }
 *
 *     &#064;Bean
 *     public DataSource dataSource() {
 *         // configure and return the necessary JDBC DataSource
 *     }
 *
 *     &#064;Bean
 *     public PlatformTransactionManager txManager() {
 *         return new DataSourceTransactionManager(dataSource());
 *     }
 *
 *     &#064;Override
 *     public PlatformTransactionManager annotationDrivenTransactionManager() {
 *         return txManager();
 *     }
 * }

example:-
http://www.concretepage.com/spring/example_transactionmanagementconfigurer_spring
http://source.lishman.com/project/689#card/5507/file/19174



Just like declarative approach, <tx:annotation-driven/> or @EnableTransactionManagement also uses TransactionInterceptor

