package com.jz.bigdata.components.elasticsearch.spring.test.test1;


/**
 * 
 * @author Dy
 *
 */

//@RunWith(Suite.class)
//@SuiteClasses( { SpringESTest.class })
//@ContextConfiguration({"classpath*:applicationContext.xml"})  


//@RunWith(SpringJUnit4ClassRunner.class)  
//@ContextConfiguration({"classpath:/spring-mybatis.xml"})  
public class SpringESTest {
	
//	@Autowired
//  public ElasticsearchCrudRepository repository;
	
//	@Autowired
//	private BookRepository repository;
	private BookRepositoryImpl repository;
	
	
//	private ElasticsearchTemplate elasticsearchTemplate;

	
	
//	@Test
	public void test(){
//	     String documentId = "123456";
//	     SampleEntity sampleEntity = new SampleEntity();
//	     sampleEntity.setId(documentId);
//	     sampleEntity.setMessage("some message");
	     
	     Book book = new Book();
	     book.setName("ES权威指南");
	     book.setPrice(49);

	     if(repository==null){
		     System.out.println("---------------------null---------------------");
	     }else{
	    	 System.out.println("---------------------error---------------------");
	    	 repository.save(book);
	     }

	}
	 
	public static void main(String[] args){
		
		new SpringESTest().test();

	}
	


}
