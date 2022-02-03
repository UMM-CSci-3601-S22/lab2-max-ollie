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
public class todosDatabase{
	private todos[] allTodos;
	public todosDatabase(String todoData) throws StreamReadException, DatabindException, IOException{
    InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(todoData));
    ObjectMapper objectMapper = new ObjectMapper();
    allTodos = objectMapper.readValue(reader, todos[].class);
	}

  public int size(){
    return allTodos.length;
  }
  public todos
}
