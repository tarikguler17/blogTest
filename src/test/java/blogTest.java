import io.restassured.RestAssured;
import org.testng.annotations.Test;
import extensions.RetryAnalyzer;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class blogTest {
    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void Test() throws IOException {
        RestAssured.baseURI = "http://localhost:5000";
        //JSONObject userObject = new JSONObject();
        String name = "tarik guler";
        String username = "atg112358";
        String email = "tarik@email.com";
        String password = "testdeneme";
        String confirm = "testdeneme";

        // Post Register

        given()
                .param("username",username)
                .param("name",name)
                .param("password",password)
                .param("email", email)
                .param("confirm",confirm)
                //.header("Accept", ContentType.JSON.getAcceptHeader())

                .when()
                .post("/register")
                .then()
                .statusCode(302);


        //Login
        String cookies =
                given()
                        .param("username", username)
                        .param("password", password)
                        .when()
                        .post("/login")
                        .then()
                        .statusCode(302)
                        .extract()
                        .cookies().values().toString();

        cookies = cookies.replace("[", "");
        cookies = cookies.replace("]", "");

        //Check Login
        String responseBody =
                given()
                        .cookie("session",cookies)
                        .when()
                        .get("/dashboard")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response().asString();
        //System.out.println(responseBody);
        System.out.println(responseBody.contains(username));

        //Add Article
        String title = "Merhaba Arkadaslar";
        String content = "Merhaba Kodluyoruz";
        given()
                .cookie("session",cookies)
                .param("title", title)
                .param("content", content)
                .when()
                .post("/addarticle")
                .then()
                .statusCode(302);

        //Check Article
        String responseBody2 =
                given()
                        .cookie("session",cookies)
                        .when()
                        .get("/articles")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response().asString();

        System.out.println(responseBody2.contains(title));

        //Logout
        given()
                .cookie("session",cookies)
                .when()
                .get("/logout")
                .then()
                .statusCode(200);

    }

}
