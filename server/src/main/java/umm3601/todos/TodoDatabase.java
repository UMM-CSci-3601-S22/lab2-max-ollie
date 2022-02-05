package umm3601.todos;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.BadRequestResponse;

/**
 * A fake "database" of todos
 */
public class TodoDatabase {
  private Todo[] allTodos;

  public TodoDatabase(String todoData) throws StreamReadException, DatabindException, IOException {
    InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(todoData));
    ObjectMapper objectMapper = new ObjectMapper();
    allTodos = objectMapper.readValue(reader, Todo[].class);
  }

  public int size() {
    return allTodos.length;
  }

  public Todo getTodo(String id) {
    return Arrays.stream(allTodos).filter(x -> x._id.equals(id)).findFirst().orElse(null);
  }

  public Todo[] listTodos(Map<String, List<String>> queryParams) {
    Todo[] filteredTodos = allTodos;

    // filter by status if defined
    if (queryParams.containsKey("status")) {
      String statusParam = queryParams.get("status").get(0);
      if (!(statusParam.equalsIgnoreCase("true") || statusParam.equalsIgnoreCase("false"))) {
        throw new BadRequestResponse("Specified status '" + statusParam + "' can't be parsed to a bool");
      }
      Boolean targetStatus = Boolean.parseBoolean(statusParam);
      filteredTodos = filterTodosByStatus(filteredTodos, targetStatus);
    }
    if (queryParams.containsKey("owner")) {
      String targetOwner = queryParams.get("owner").get(0);
      filteredTodos = filterTodosByOwner(filteredTodos, targetOwner);
    }
    if (queryParams.containsKey("category")) {
      String targetCategory = queryParams.get("category").get(0);
      filteredTodos = filterTodosByCategory(filteredTodos, targetCategory);
    }
    if (queryParams.containsKey("contains")) {
      String targetBody = queryParams.get("contains").get(0);
      filteredTodos = filterTodosByContains(filteredTodos, targetBody);
    }
    if (queryParams.containsKey("limit")) {
      String limitParam = queryParams.get("limit").get(0);
      try {
        int targetLimit = Integer.parseInt(limitParam);
        filteredTodos = filterTodosByLimit(filteredTodos, targetLimit);
      } catch (NumberFormatException e) {
        throw new BadRequestResponse("Specified limit '" + limitParam + "' can't be parsed to an integer");
      }
    }
    if (queryParams.containsKey("orderBy")) {
      String targetOrder = queryParams.get("orderBy").get(0);
      filteredTodos = orderTodos(filteredTodos, targetOrder);
    }
    return filteredTodos;
  }

  private Todo[] orderTodos(Todo[] todos, String targetOrder) {
    switch (targetOrder) {
      case "owner":
        Arrays.sort(todos, new Comparator<Todo>() {
          public int compare(Todo t1, Todo t2) {
            return t1.owner.compareTo(t2.owner);
          }
        });
        break;
      case "body":
        Arrays.sort(todos, new Comparator<Todo>() {
          public int compare(Todo t1, Todo t2) {
            return t1.body.compareTo(t2.body);
          }
        });
        break;
      case "category":
        Arrays.sort(todos, new Comparator<Todo>() {
          public int compare(Todo t1, Todo t2) {
            return t1.category.compareTo(t2.category);
          }
        });
        break;
      case "status":
        Arrays.sort(todos, new Comparator<Todo>() {
          public int compare(Todo t1, Todo t2) {
            return Boolean.compare(t1.status, t2.status);
          }
        });
        break;
        default:
        return todos;
    }

    return todos;
  }

  private Todo[] filterTodosByContains(Todo[] todos, String targetBody) {
    return Arrays.stream(todos).filter(x -> x.body.contains(targetBody)).toArray(Todo[]::new);
  }

  private Todo[] filterTodosByLimit(Todo[] todos, int targetLimit) {
    return Arrays.stream(todos).limit(targetLimit).toArray(Todo[]::new);
  }

  private Todo[] filterTodosByStatus(Todo[] todos, Boolean targetStatus) {
    return Arrays.stream(todos).filter(x -> x.status == targetStatus).toArray(Todo[]::new);
  }

  public Todo[] filterTodosByOwner(Todo[] todos, String targetOwner) {
    return Arrays.stream(todos).filter(x -> x.owner.equalsIgnoreCase(targetOwner)).toArray(Todo[]::new);
  }

  public Todo[] filterTodosByCategory(Todo[] todos, String targetCategory) {
    return Arrays.stream(todos).filter(x -> x.category.equalsIgnoreCase(targetCategory)).toArray(Todo[]::new);
  }

}
