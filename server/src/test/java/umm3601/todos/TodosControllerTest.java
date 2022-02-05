package umm3601.todos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import io.javalin.http.NotFoundResponse;

import umm3601.Server;

public class TodosControllerTest {

  private Context ctx = mock(Context.class);

  private TodoController TodoController;
  private static TodoDatabase db;

  @BeforeEach
  public void setUp() throws IOException {
    ctx.clearCookieStore();

    db = new TodoDatabase(Server.Todo_DATA_FILE);
    TodoController = new TodoController(db);
  }

  /**
   * Confirms that we can get all the Todos.
   *
   * @throws IOException
   */
  @Test
  public void canGetAllTodos() throws IOException {
    TodoController.getTodos(ctx);

    // Confirm that `json` was called with all the Todos.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    assertEquals(db.size(), argument.getValue().length);
  }

  @Test
  public void canGetTodosWithOwnerBarry() throws IOException {
    // Add a query param map to the context that maps "owner"
    // to "Barry".
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("owner", Arrays.asList(new String[] { "Barry" }));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    // Call the method on the mock controller with the added
    // query param map to limit the result to just todos with
    // owner Barry.
    TodoController.getTodos(ctx);

    // Confirm that all the todos passed to `json` have owner Barry.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertEquals("Barry", todo.owner);
    }
  }

  /**
   * Test that if the todo sends a request with an illegal value in
   * the status field we get a reasonable error code back.
   */
  @Test
  public void respondsAppropriatelyToIllegalStatus() {
    // We'll set the requested "status" to be a string ("tralse")
    // that can't be parsed
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("status", Arrays.asList(new String[] { "tralse" }));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    // This should now throw a `BadRequestResponse` exception because
    // our request has a status that can't be parsed
    Assertions.assertThrows(BadRequestResponse.class, () -> {
      TodoController.getTodos(ctx);
    });
  }

  @Test
  public void respondsAppropriatelyToRequestForNonexistentId() throws IOException {
    when(ctx.pathParam("id")).thenReturn(null);
    Assertions.assertThrows(NotFoundResponse.class, () -> {
      TodoController.getTodo(ctx);
    });
  }

  @Test
  public void canGetTodoWithSpecifiedId() throws IOException {
    String id = "588959855f1ee021726da5f9";
    Todo todo = db.getTodo(id);

    when(ctx.pathParam("id")).thenReturn(id);

    TodoController.getTodo(ctx);

    verify(ctx).json(todo);
    verify(ctx).status(HttpCode.OK);
  }

  @Test
  public void canGetTodosWithGivenStatusAndCategory() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("category", Arrays.asList(new String[] { "homework" }));
    queryParams.put("status", Arrays.asList(new String[] { "true" }));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    TodoController.getTodos(ctx);

    // Confirm that all the todos passed to `json` are homework
    // and have status true.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertEquals(true, todo.status);
      assertEquals("homework", todo.category);
    }
  }
  @Test
  public void canGetTodosWithContains() throws IOException {
    // Add a query param map to the context that maps "contains" to "cillum"
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("contains", Arrays.asList(new String[] { "cillum" }));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    // Call the method on the mock controller with the added
    // query param map to limit the result to just todos with
    // cillum in body.
    TodoController.getTodos(ctx);

    // Confirm that all the todos passed to `json` have cillum in body.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertTrue(todo.body.contains("cillum"));
    }
  }
  @Test
  public void canGetLimitedTodos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("limit", Arrays.asList(new String[] { "7" }));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    TodoController.getTodos(ctx);

    // Confirm that `json` was called with all the Todos.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    int b = 0;
    for(Todo todo: argument.getValue()) {
      b++;
    }
    assertEquals(7, b);
  }
  @Test
  public void canFailBadLimitedTodos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("limit", Arrays.asList(new String[] { "jump" }));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    Assertions.assertThrows(BadRequestResponse.class, () -> {
      TodoController.getTodos(ctx);
    });
  }
  @Test
  public void canGetTodosAlphabeticallyByOwner() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] { "owner" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);

    TodoController.getTodos(ctx);

    // Confirm that all the todos passed to `json` are ordered by Owner
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    Todo[] a = argument.getValue();
    for(int i = 0; i <a.length-1;i++){
      assertTrue(a[i].owner.compareTo(a[i+1].owner)<=0);
    }
  }
  @Test
  public void canGetTodosAlphabeticallyByBody() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] { "body" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);

    TodoController.getTodos(ctx);

    // Confirm that all the todos passed to `json` are ordered by Body
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    Todo[] a = argument.getValue();
    for(int i = 0; i <a.length-1;i++){
      assertTrue(a[i].body.compareTo(a[i+1].body)<=0);
    }
  }
  @Test
  public void canGetTodosAlphabeticallyByCategory() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] { "category" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);

    TodoController.getTodos(ctx);

    // Confirm that all the todos passed to `json` are ordered by Category
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    Todo[] a = argument.getValue();
    for(int i = 0; i <a.length-1;i++){
      assertTrue(a[i].category.compareTo(a[i+1].category)<=0);
    }
  }
  @Test
  public void canGetTodosAlphabeticallyByStatus() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] { "status" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);

    TodoController.getTodos(ctx);

    // Confirm that all the todos passed to `json` are ordered by Status
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    Todo[] a = argument.getValue();
    for(int i = 0; i <a.length-1;i++){
      assertTrue(Boolean.compare(a[i].status,a[i+1].status)<=0);
    }
  }
}
