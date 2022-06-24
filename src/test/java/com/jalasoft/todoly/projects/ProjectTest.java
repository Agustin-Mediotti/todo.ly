package com.jalasoft.todoly.projects;

import api.APIManager;
import api.methods.APIProjectMethods;
import entities.NewProject;
import entities.Project;
import framework.Environment;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ProjectTest {
    private static final Environment environment = Environment.getInstance();
    private static final APIManager apiManager = APIManager.getInstance();
    private int projectId;

    @BeforeClass
    public void setup() {
        apiManager.setCredentials(environment.getUserName(), environment.getPassword());
    }

    @Test
    public void getAllProjects() {
        Reporter.log("Verify that a 200 OK status and a correct response body result when a GET request to the \"/projects.json\" endpoint is executed", true);
        Response response = apiManager.get(environment.getProjectEndPoint());

        Assert.assertEquals(response.getStatusCode(), 200, "Correct status code is not returned");
        Assert.assertTrue(response.getStatusLine().contains("200 OK"), "Correct status code and message");
        Assert.assertFalse(response.getBody().asString().contains("ErrorMessage"), "Correct response body is");
        Assert.assertFalse(response.getBody().asString().contains("ErrorCode"), "Correct response body is not returned");
    }

    @Test
    public void createNewProject() {
        NewProject newProject = new NewProject("Testing Project", 2);
        Response response = apiManager.post(environment.getProjectEndPoint(), ContentType.JSON, newProject);
        Project responseProject = response.as(Project.class);
        projectId = responseProject.getId();

        Assert.assertEquals(response.getStatusCode(), 200, "Correct status code is not returned");
        Assert.assertTrue(response.getStatusLine().contains("200 OK"), "Correct status code and message is not returned");
        Assert.assertNull(response.jsonPath().getString("ErrorMessage"), "Error message was returned");
        Assert.assertNull(response.jsonPath().getString("ErrorCode"), "Error code was returned");
        Assert.assertEquals(responseProject.getContent(), newProject.getContent(), "Incorrect Content value was set");
        Assert.assertEquals(responseProject.getIcon(), newProject.getIcon(), "Incorrect Icon value was set");
    }

    @Test
    public void tooShortProjectName() {
        NewProject newProject = new NewProject("", 2);
        Response response = apiManager.post(environment.getProjectEndPoint(), ContentType.JSON, newProject);

        Assert.assertEquals(response.getStatusCode(), 200, "Correct status code is not returned");
        Assert.assertTrue(response.getStatusLine().contains("200 OK"), "Correct status code and message is not returned");
        Assert.assertNotNull(response.jsonPath().getString("ErrorMessage"), "Error Message was not returned");
        Assert.assertNotNull(response.jsonPath().getString("ErrorCode"), "Error Code was not returned");
        Assert.assertEquals(response.jsonPath().getString("ErrorMessage"), "Too Short Project Name", "Incorrect Error Message was returned");
        Assert.assertEquals(response.jsonPath().getString("ErrorCode"), "305", "Incorrect Error Code was returned");
    }

    @AfterClass
    public void tearDown() {
        if (projectId != 0) {
            boolean isProjectDeleted = APIProjectMethods.deleteProject(projectId);
            Assert.assertTrue(isProjectDeleted, "Project was not deleted");
        }
    }
}
