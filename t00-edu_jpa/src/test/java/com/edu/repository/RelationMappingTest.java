package com.edu.repository;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.edu.entity.Board;
import com.edu.entity.Member;
import com.edu.entity.Order;
import com.edu.entity.Product;
import com.edu.entity.Profile;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class RelationMappingTest {
	
	@Autowired
	private BoardRepository2 boardRepo;
	
	@Autowired
	private MemberRepository memberRepo;

	@Test
	@DisplayName("게시글 등록 테스트")
	public void testManyToOneInsert() {
		Member user1 = new Member();
		user1.setId("user1");
		user1.setPassword("1111");
		user1.setName("홍길동");
		user1.setRole("member");
		memberRepo.save(user1);
		
		Member user2 = new Member();
		user2.setId("user2");
		user2.setPassword("222");
		user2.setName("이순신	");
		user2.setRole("admin");
		memberRepo.save(user2);
		
		for (int i = 0; i <= 3; i++) {
			Board board = new Board();
			board.setMember(user1);
			board.setTitle("홍길동 제목" + i);
			board.setContent("홍길동 내용 "  + i);
			boardRepo.save(board);
		}
		
		for (int i = 0; i <= 3; i++) {
			Board board = new Board();
			board.setMember(user2);
			board.setTitle("이순신 제목" + i);
			board.setContent("이순신 내용 "  + i);
			boardRepo.save(board);
		}
	}
	
	@Test
	public void ManyToOneTest() {
		Board board = boardRepo.findById(1L).get();
		System.out.println(board);
	}
	
	@Test
	public void TwoWayMappingTest() {
		Member member = memberRepo.findById("user1").get();
		List<Board> boardList = member.getBoardList();
		boardList.forEach(System.out::println);
	}
	
	@Test
	public void testCascade() { // cascade 설정 전
		Member user1 = new Member();
		user1.setId("user3");
		user1.setPassword("3333");
		user1.setName("유관순");
		user1.setRole("member");
		
		for (int i = 0; i <= 3; i++) {
			Board board = new Board();
			board.setMember(user1);
			board.setTitle("유관순 제목 " + i);
			board.setContent("유관순 내용 " + i);
			// boardRepo.save(board);
		}
		
		memberRepo.save(user1);
	}
	
	@Test
	public void testCascade2() { // cascade 설정 후
		Member user1 = new Member();
		user1.setId("user4");
		user1.setPassword("4444");
		user1.setName("강감찬");
		user1.setRole("member");
		
		for (int i = 0; i <= 3; i++) {
			Board board = new Board();
			board.setMember(user1);
			board.setTitle("강감찬 제목 " + i);
			board.setContent("강감찬 내용 " + i);
			// boardRepo.save(board); 이게 없어도 실행됨
		}
		
		memberRepo.save(user1);
	}
	
	@Autowired
	ProfileRepository profileRepo;
	
	@Test
	public void testOneToOne() {
		Member member = memberRepo.findById("user1").get();
		
		Profile profile = new Profile();
		profile.setMember(member);
		profile.setTel("010-123-4567");
		profile.setAddress("서울 강남구 논현동");
		
		Profile savedProfile = profileRepo.save(profile);
		System.out.println(savedProfile);
	}
	
	@Test
	public void testOneToOne2() {
		Member member = memberRepo.findById("user1").get();
		System.out.println(member);
	}
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private OrderRepository orderRepo;
	
	@Test
	public void testManyToMany() {
		// 상품 등록
		Product product1 = new Product("Galaxy S23");
		Product product2 = new Product("iPhone 14Pro");
		
		productRepo.save(product1);
		productRepo.save(product2);
		
		// 주문 등록
		Order order = new Order();
		order.setOrderDate(new Date());
		order.getProductList().add(product1);
		order.getProductList().add(product2);
		
		Order savedOrder = orderRepo.save(order);
		System.out.println(savedOrder);
	}
}
