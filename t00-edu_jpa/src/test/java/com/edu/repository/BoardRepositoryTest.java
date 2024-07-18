package com.edu.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.edu.entity.BoardEntity;

import jakarta.transaction.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BoardRepositoryTest {
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Test
	@DisplayName("게시글 등록이 정상 동작한다")
	public void insertBoardText() {
		// given
		BoardEntity board = BoardEntity.builder()
									.title("JPA")
									.writer("이순신")
									.content("자바 표준 ORM 스펙입니다.")
									.build();
		// when
		BoardEntity savedBoard = boardRepository.save(board); // insert로 동작
		
		// then
		assertThat(savedBoard.getTitle()).isEqualTo(board.getTitle());
	}
	
	@Test
	@DisplayName("게시글 추출이 정상 동작한다.")
	public void getBoardTest() {
		Optional<BoardEntity> board = boardRepository.findById(1L);
		assertTrue(board.isPresent());
	}
	
	@Test
	@DisplayName("게시글 수정이 정상 동작한다.")
	public void updateBoardTest() throws Exception {
		BoardEntity board = boardRepository.findById(1L).orElseThrow(Exception::new);
		board.setTitle("AWS");
		board.setContent("아마존 클라우드 서비스");
		
		BoardEntity savedBoard = boardRepository.save(board); // update로 동작
		
		assertThat(savedBoard.getTitle()).isEqualTo("AWS");
	}
	
	@Test
	@DisplayName("게시글 전체 조회가 정상 동작한다.")
	public void getBoardListTest() {
		List<BoardEntity> boardList = boardRepository.findAll();
		boardList.forEach(System.out::println);
	}
	
	@Test
	@DisplayName("게시글 삭제가 정상 동작한다")
	private void deleteBoardTest() {
		// when
		boardRepository.deleteById(1L);
		boolean exists = boardRepository.existsById(1L);
		
		// then
		assertFalse(exists);
	}
	
	@Test
	public void createBoardList() {
		Random random = new Random();
		String[] names = {"홍길동", "이순신", "유관순"};
		for (int i = 10; i <= 20; i++) {
			BoardEntity board = new BoardEntity();
			board.setTitle("테스트 제목 " + i);
			board.setWriter(names[random.nextInt(3)]);
			board.setContent("테스트 내용 " + i);
			board.setCnt(random.nextInt(30));
			boardRepository.save(board);
		}
	}
	
	@Test
	@DisplayName("작성자(writer) 조건으로 조회가 정상 동작한다.")
	public void findByWriterTest() {
		this.createBoardList();
		List<BoardEntity> boardList = boardRepository.findByWriter("홍길동");
		boardList.forEach(System.out::println);
	}
	
	@Test
	@DisplayName("제목(title) 일부 조건으로 조회가 정상 동작한다.")
	public void findByTitleContainingTest() {
		List<BoardEntity> boardList = boardRepository.findByTitleContaining("테스트");
		boardList.forEach(System.out::println);
	}
	
	@Test
	@DisplayName("제목(title) or 내용(content) 조건으로 조회가 정상 동작한다.")
	public void findByTitleOrContentContainingTest() {
		List<BoardEntity> boardList = boardRepository.findByTitleOrContent("테스트 제목 1", "테스트 내용 2");
		boardList.forEach(System.out::println);
	}
	
	@Test
	@DisplayName("조회수(cnt) 크기 비교 조건으로 조회가 정상 동작한다.")
	public void findByCntGreaterThanTest() {
		List<BoardEntity> boardList = boardRepository.findByCntGreaterThan(5);
		boardList.forEach(System.out::println);
	}
	
	@Test
	@DisplayName("조회수(cnt) 범위 지정 조건으로 조회가 정상 동작한다.")
	public void findByWriterInTest() {
		List<String> writers = Arrays.asList("이순신", "유관순");
		List<BoardEntity> boardList = boardRepository.findByWriterIn(writers);
		boardList.forEach(System.out::println);
	}
	
	@Test
	@DisplayName("exists~By: 특정 데이터가 존재하는지 확인한다")
	public void existsByWriterTest() {
		assertTrue(boardRepository.existsByWriter("홍길동"));
	}
	
	@Test
	@DisplayName("조회 쿼리를 수행한 후 쿼리 결과로 나온 레코드수를 리턴한다")
	public void countByWriterTest() {
		assertThat(boardRepository.countByWriter("홍길동")).isEqualTo(2);
	}

	@Test
	@Transactional
	@DisplayName("삭제쿼리 수행. 리턴값이 없거나 삭제한 횟수 리턴한다")
	public void deleteByWriterTest() {
		boardRepository.deleteByWriter("홍길동");
		assertThat(boardRepository.existsByWriter("홍길동")).isFalse();
	}

	@Test
	@Transactional
	@Rollback(false)
	@DisplayName("삭제쿼리 수행. 리턴값이 없거나 삭제한 횟수 리턴한다")
	public void deleteByWriterTest2() {
		boardRepository.deleteByWriter("홍길동");
		assertThat(boardRepository.existsByWriter("홍길동")).isFalse();
	}
	
	@Test
	@DisplayName("홍길동이 작성한 게시글 중에서 조회수가 10건 이상인 게시글 조회")
	public void findByWriterAndCntGreaterThanTest() {
		List<BoardEntity> boardList = boardRepository.findByWriterAndCntGreaterThan("홍길동", 10);
		boardList.forEach(System.out::println);
	}
	
	@Test
	@DisplayName("작가가 작성한 게시물 정렬해서 조회")
	public void findByWriterOrderByCntTest() {
		List<BoardEntity> boardList = boardRepository.findByWriterOrderByCntDesc("홍길동");
		boardList.forEach(System.out::println);
	}
	
	@Test
	@DisplayName("Sort 매개변수 정렬 조건이 정상 동작한다.")
	public void findWriterTest() {
		List<BoardEntity> boardList = 
				boardRepository.findByWriter("홍길동", Sort.by(Sort.Direction.DESC, "cnt"));
		boardList.forEach(System.out::println);
	}
	
	@Test
	@DisplayName("Sort 매개변수 2차 정렬 조건이 정상 동작한다.")
	public void findAllTest() {
		List<BoardEntity> boardList = 
				boardRepository.findAll(Sort.by(Sort.Order.desc("cnt"), Sort.Order.asc("seq")));
		boardList.forEach(System.out::println);
	}
	
	@Test
	@DisplayName("Paging 처리 조건이 정상 동작한다.")
	public void findAllPagingTest() {
		Page<BoardEntity> pages = 
				boardRepository.findAll(PageRequest.of(0, 5, Sort.Direction.DESC, "cnt"));
		pages.forEach(System.out::println);
		
		System.out.println(pages);
	}
	
	@Test
	public void boardQuery1Test() {
		List<BoardEntity> boardList = boardRepository.boardQuery1("테스트");
		boardList.forEach(System.out::println);
	}
	
	@Test
	public void boardQuery2Test() {
		List<Object[]> boardList = boardRepository.boardQuery2("테스트");
		boardList.forEach((row) -> {
			System.out.println(Arrays.toString(row));
		});
	}
}
