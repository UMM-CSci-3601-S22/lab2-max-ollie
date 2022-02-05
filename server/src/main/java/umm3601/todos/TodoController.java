package umm3601.todos;

import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import io.javalin.http.NotFoundResponse;

public class TodoController {
  private TodoDatabase data;

  public TodoController(TodoDatabase data) {
    this.data = data;
  }

  public void getTodo(Context context) {
    String id = context.pathParam("id");
    Todo todo = data.getTodo(id);
    if (todo != null) {
      context.json(todo);
      context.status(HttpCode.OK);
    } else {
      throw new NotFoundResponse("No todo with id" + id);
    }
  }

  public void getTodos(Context ctx) {
    Todo[] todos = data.listTodos(ctx.queryParamMap());
    ctx.json(todos);
  }
}
