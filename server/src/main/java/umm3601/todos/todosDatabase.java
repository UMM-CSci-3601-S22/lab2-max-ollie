package umm3601.todos;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.BadRequestResponse;

/**
 * A fake "database" of todos
 */
public class todosDatabase {
  private todo[] allTodos;

  public todosDatabase(String todoData) throws StreamReadException, DatabindException, IOException {
    InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(todoData));
    ObjectMapper objectMapper = new ObjectMapper();
    allTodos = objectMapper.readValue(reader, todo[].class);
  }

  public int size(){
    return allTodos.length;
  }

  public todo getTodo(String id) {
    return Arrays.stream(allTodos).filter(x -> x._id.equals(id)).findFirst().orElse(null);
  }

  public todo[] listTodos(Map<String, List<String>> queryParams) {
    todo filteredTodos[] = allTodos;

    // filter by status if defined
    if (queryParams.containsKey("status")) {
      String statusParam = queryParams.get("status").get(0);
      try {
        Boolean targetStatus = Boolean.parseBoolean(statusParam);
        filteredTodos = filterTodosByStatus(filteredTodos, targetStatus);
      } catch (NumberFormatException e) {
        throw new BadRequestResponse("Specified status '" + statusParam + "' can't be parsed to a bool");
      }
    }
    if (queryParams.containsKey("owner")) {
      String targetOwner = queryParams.get("owner").get(0);
      filteredTodos = filterTodosByOwner(filteredTodos, targetOwner);
    }
    if (queryParams.containsKey("category")) {
      String targetCategory = queryParams.get("category").get(0);
      filteredTodos = filterTodosByCategory(filteredTodos, targetCategory);
    }
    return filteredTodos;
  }

  private todo[] filterTodosByStatus(todo[] todos, Boolean targetStatus) {
    return Arrays.stream(todos).filter(x -> x.status == targetStatus).toArray(todo[]::new);
  }

  public todo[] filterTodosByOwner(todo[] todos, String targetOwner) {
    return Arrays.stream(todos).filter(x -> x.owner.equals(targetOwner)).toArray(todo[]::new);
  }

  public todo[] filterTodosByCategory(todo[] todos, String targetCategory) {
    return Arrays.stream(todos).filter(x -> x.category.equals(targetCategory)).toArray(todo[]::new);
  }


}
