package umm3601.todos;

import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import io.javalin.http.NotFoundResponse;

public class todosController{
  private todosDatabase data;
  public todosController(todosDatabase data){
    this.data = data;
  }
  public void getTodo(Context context ){
    String id = context.pathParam("id");
    todo todo = data.getTodo(id);
    if(todo!= null){
      context.json(todo);
      context.status(HttpCode.OK);
    }else{
      throw new NotFoundResponse("No todo with id"+ id);
    }
  }
  public void getUsers(Context ctx) {
    todo[] todos = data.listTodos(ctx.queryParamMap());
    ctx.json(todos);
  }
}
