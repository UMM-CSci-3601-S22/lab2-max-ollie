package umm3601;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.javalin.http.HttpCode;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import umm3601.user.UserDatabase;

/**
 * A set of simple functional tests that ensure that the
 * key *user* functionality of our server works.
 *
 * This isn't complete – we don't yet check that
 * appropriate errors are returns when we try, e.g.,
 * to get a user with an unknown or invalid ID.
 */
public class UserFunctionalSpec {
  private static ObjectMapper objectMapper;
  private static UserDatabase userDatabase;

  @BeforeAll
  public static void startServer() {
    Server.main(null);
  }

  @AfterAll
  public static void stopServer() {
    Server.stopServer();
  }

  @BeforeAll
  public static void createDbAndMapper() throws IOException {
    objectMapper = new ObjectMapper();
    userDatabase = new UserDatabase(Server.USER_DATA_FILE);
  }

  @Test
  public void canGetAllUsers() throws JsonProcessingException {
    String usersJson = objectMapper.writeValueAsString(userDatabase.listUsers(new HashMap<String, List<String>>()));
    HttpResponse<String> response = Unirest.get("http://localhost:4567/api/users").asString();
    assertEquals(HttpCode.OK.getStatus(), response.getStatus(), "Getting all the users should return OK status");
    /*
     * We don't want to just compare strings directly, because the order of fields
     * in a JSON object isn't specified, and two "equal" objects could have their
     * fields in different orders. If we just did a pure String comparison
     * we'd get that wrong; if we use the Jackson ObjectMapper to turn them into
     * JSON objects and compare *those*, we we avoid that problem.
     *
     * See this for more examples and info:
     * https://www.baeldung.com/jackson-compare-two-json-objects
     */
    assertEquals(objectMapper.readTree(usersJson),
        objectMapper.readTree(response.getBody()),
        "The list of users didn't match");
  }

  @Test
  public void canGetSingleUserById() throws JsonProcessingException {
    String id = "588935f5c668650dc77df581";
    String user = objectMapper.writeValueAsString(userDatabase.getUser(id));
    HttpResponse<String> response = Unirest.get("http://localhost:4567/api/users/" + id).asString();
    assertEquals(HttpCode.OK.getStatus(), response.getStatus(), "Getting an existing user should return OK status");
    /*
     * We don't want to just compare strings directly, because the order of fields
     * in a JSON object isn't specified, and two "equal" objects could have their
     * fields in different orders. If we just did a pure String comparison
     * we'd get that wrong; if we use the Jackson ObjectMapper to turn them into
     * JSON objects and compare *those*, we we avoid that problem.
     *
     * See this for more examples and info:
     * https://www.baeldung.com/jackson-compare-two-json-objects
     */
    assertEquals(objectMapper.readTree(user), objectMapper.readTree(response.getBody()), "The user didn't match");
  }
}
