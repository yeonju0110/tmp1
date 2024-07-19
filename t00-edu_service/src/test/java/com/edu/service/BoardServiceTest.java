package com.edu.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.edu.dto.BoardDto;
import com.edu.entity.Board;
import com.edu.repository.BoardRepository;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
public class BoardServiceTest {
	
	@Autowired
	private BoardService boardService;
	
	@Autowired
	private BoardRepository boardRepo;
	
	public BoardDto saveBoardDto() {
		BoardDto boardDto = new BoardDto();
		boardDto.setTitle("MVC");
		boardDto.setWriter("홍길동");
		boardDto.setContent("Model, View, Controller 디자인 패턴이다");
		return boardDto;
	}
	
	@Test
	public void insertBoardTest() {
		BoardDto boardDto = this.saveBoardDto();
		long seq = boardService.insertBoard(boardDto);
		
		Board board = boardRepo.findById(seq).orElseThrow(EntityNotFoundException::new);
		
		assertThat(board.getWriter()).isEqualTo(boardDto.getWriter());
		assertThat(board.getTitle()).isEqualTo(boardDto.getTitle());
		assertThat(board.getContent()).isEqualTo(boardDto.getContent());
	}
	
	@Test
	public void updateBoardTest() {
		BoardDto boardDto = new BoardDto();
		boardDto.setSeq(1L);
		boardDto.setTitle("Spring");
		boardDto.setContent("Backend Java 프레임워크이다");
		
		long seq = boardService.updateBoard(boardDto);
		
		Board board = boardRepo.findById(seq).orElseThrow(EntityNotFoundException::new);
		
		assertThat(board.getTitle()).isEqualTo(boardDto.getTitle());
		assertThat(board.getContent()).isEqualTo(boardDto.getContent());
	}
	
	@Test
	public void getBoardTest() {
		long seq = 1L;
		BoardDto boardDto = boardService.getBoard(seq);
		System.out.println(boardDto);
	}
	
	@Test
	public void getBoardListTest() {
		BoardDto boardDto = this.saveBoardDto();
		boardService.insertBoard(boardDto);
		
		List<BoardDto> boardList = boardService.getBoardList();
		
		boardList.forEach(System.out::println);
	}
	
	@Test
	public void deleteBoardTest() {
		long seq = 1L;
		boardService.deleteBoard(seq);
		
		Optional<Board> board = boardRepo.findById(seq);
		
		assertThat(board.isPresent()).isFalse();
	}
}
