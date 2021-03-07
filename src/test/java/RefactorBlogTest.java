import extensions.RetryAnalyzer;
import io.restassured.RestAssured;
import org.testng.annotations.Test;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class RefactorBlogTest {
    private String cookies;

    private void register(String username, String name, String email, String password, String confirm)
            throws IOException {
        given()
                .param("username",username)
                .param("name",name)
                .param("password",password)
                .param("email", email)
                .param("confirm",confirm)
                .when()
                .post("/register")
                .then()
                .statusCode(302);
    }
    private void loginUser(String username,String password) throws IOException{
        String cookies2 =
                given()
                        .param("username", username)
                        .param("password", password)
                        .when()
                        .post("/login")
                        .then()
                        .statusCode(302)
                        .extract()
                        .cookies().values().toString();

        cookies2 = cookies2.replace("[", "");
        cookies2 = cookies2.replace("]", "");
        setCookies(cookies2);
    }
    public void setCookies(String cookies){
        this.cookies = cookies;
    }
    public String getCookies(){
        return this.cookies;
    }

    private void checkLogin(String username) throws IOException{
        String responseBody =
                given()
                        .cookie("session",getCookies())
                        .when()
                        .get("/dashboard")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response().asString();

        System.out.println(responseBody.contains(username));
    }
    private void addArticle(String title,String content) throws IOException{
        given()
                .cookie("session",getCookies())
                .param("title", title)
                .param("content", content)
                .when()
                .post("/addarticle")
                .then()
                .statusCode(302);
    }
    private void checkArticle(String title) throws IOException{
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
    }
    private void logOut() throws IOException{
        given()
                .cookie("session", getCookies())
                .when()
                .get("/logout")
                .then()
                .statusCode(200);
    }
    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void Test() throws IOException {
        RestAssured.baseURI = "http://localhost:5000";
        String name = "tarik guler";
        String username = "atg112358";
        String email = "tarik@email.com";
        String password = "testdeneme";
        String confirm = "testdeneme";
        String title = "Merhaba Arkadaslar";
        String content = "Merhaba Kodluyoruz";

        register(username, name, email, password, confirm);
        loginUser(username, password);
        checkLogin(username);
        addArticle(title, content);
        checkArticle(title);
        logOut();
    }
}