package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.model.TodoEntity;
import org.example.model.TodoRequest;
import org.example.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

// 목을 사용하는 이유
// 1. 외부 시스템에 의존하지 않고 자체 테스트를 실행할 수 있어야 하기 때문
//    : 유닛 테스트는 네트워크나 데이터베이스 연결이 안된다고 해서 유닛 테스트도 함께 실행이 불가능하면 안됨
// 2. 실제 데이터베이스를 사용하게 되면 테스트할 때마다 db에 값이 막 추가, 수정, 삭제되는 일이 발생할 수 있음
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private  TodoService todoService;

    @Test
    void add() {
        when(this.todoRepository.save(any(TodoEntity.class)))
                .then(AdditionalAnswers.returnsFirstArg());

        TodoRequest expected = new TodoRequest();
        expected.setTitle("Test Title");

        TodoEntity actual = this.todoService.add(expected);

        assertEquals(expected.getTitle(), actual.getTitle());
    }

    @Test
    void searchById() {
        TodoEntity entity = new TodoEntity();
        entity.setId(123L);
        entity.setTitle("TITLE");
        entity.setOrder(0L);
        entity.setCompleted(false);
        Optional<TodoEntity> optional = Optional.of(entity);

        given(this.todoRepository.findById(anyLong()))
                .willReturn(optional);

        TodoEntity actual = this.todoService.searchById(123L);

        TodoEntity expected = optional.get();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getOrder(), actual.getOrder());
        assertEquals(expected.getCompleted(), actual.getCompleted());
    }

    @Test
    public void searchByIdFailed() {
        given(this.todoRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            this.todoService.searchById(123L);
        });
    }

}