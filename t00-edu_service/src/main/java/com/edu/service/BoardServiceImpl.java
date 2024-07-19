package com.edu.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edu.dto.BoardDto;
import com.edu.entity.Board;
import com.edu.repository.BoardRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

// C,U,D할 때는 data가 변경됨. 트랜잭션 매니징을 잘 해줘야함.

@Service
@Transactional
@RequiredArgsConstructor // 생성자 인젝션, 객체 생성될 때 인젝션 됨
public class BoardServiceImpl implements BoardService {
	
	private final BoardRepository boardRepo; // 한번 생성되면 안바뀜. 스프링 컨테이너가 구독될 때

	public Long insertBoard(BoardDto dto) {
		Board board = dto.createBoard();
		Board savedBoard = boardRepo.save(board);
		return savedBoard.getSeq();
	}

	/**
	 * save()가 없어도 수정됨. findById할 때 1차 캐싱
	 * -> 복사본은 수정된거
	 * -> 두개가 불일치해서 service로직이 끝날때 update실행됨
	 */
	public Long updateBoard(BoardDto dto) {
		Board board = boardRepo.findById(dto.getSeq()).orElseThrow(EntityNotFoundException::new);
		board.updateBoard(dto);
		return board.getSeq();
	}

	/**
	 * findById 호출안하고 delete만 있으면 에러남
	 * findById 로 인해 persistent context에 등록되어서 삭제됨
	 */
	public void deleteBoard(Long seq) {
		Board board = boardRepo.findById(seq).orElseThrow(EntityNotFoundException::new);
		boardRepo.delete(board);
	}

	@Transactional(readOnly = true) // read는 트랜잭션 관리 필요없어서 선언 -> 부하 줄여줌
	public BoardDto getBoard(Long seq) {
		Board board = boardRepo.findById(seq).orElseThrow(EntityNotFoundException::new);
		return BoardDto.of(board);
	}

	@Transactional(readOnly = true)
	public List<BoardDto> getBoardList() {
		List<Board> boardList = boardRepo.findAll();
		List<BoardDto> boardDtoList = new ArrayList<>();
		for (Board board : boardList) {
			BoardDto boardDto = BoardDto.of(board);
			boardDtoList.add(boardDto);
		}
		return boardDtoList;
	}

	
}
