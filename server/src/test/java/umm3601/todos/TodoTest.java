package umm3601.todos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class TodoTest {
  @Test
  public void get58895985a22c04e761776d54() throws IOException {
    TodoDatabase db = new TodoDatabase("/todos.json");
    Todo td = db.getTodo("58895985a22c04e761776d54");
    assertEquals("Blanche", td.owner, "Incorrect owner");
    assertEquals(false, td.status, "Incorrect status");
    assertEquals("software design", td.category, "Incorrect category");
  }

  @Test
  public void getStokesClayton() throws IOException {
    TodoDatabase db = new TodoDatabase("/todos.json");
    Todo td = db.getTodo("58895985f13555dedae2cf6f");
    assertEquals("Workman", td.owner, "Incorrect owner");
    assertEquals(false, td.status, "Incorrect status");
    assertEquals("homework", td.category, "Incorrect category");
  }
}


