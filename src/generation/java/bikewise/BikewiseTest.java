package bikewise;

import org.junit.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.FixMethodOrder;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import org.junit.runners.MethodSorters;
import io.qameta.allure.restassured.AllureRestAssured;
import es.us.isa.restest.testcases.restassured.filters.StatusCode5XXFilter;
import es.us.isa.restest.testcases.restassured.filters.NominalOrFaultyTestCaseFilter;
import java.io.File;
import es.us.isa.restest.testcases.restassured.filters.ResponseValidationFilter;
import es.us.isa.restest.testcases.restassured.filters.CSVFilter;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BikewiseTest {

	private static final String OAI_JSON_URL = "src/test/resources/Bikewise/swagger.yaml";
	private final ResponseValidationFilter validationFilter = new ResponseValidationFilter(OAI_JSON_URL);
	private StatusCode5XXFilter statusCode5XXFilter = new StatusCode5XXFilter();
	private AllureRestAssured allureFilter = new AllureRestAssured();
	private final String APIName = "bikewise";

	@BeforeClass
 	public static void setUp() {
		RestAssured.baseURI = "https://bikewise.org/api";
	}

	@Test
	public void test_qxi9qptuouuo_GETversionlocationsmarkersformat() {
		String testResultId = "test_qxi9qptuouuo_GETversionlocationsmarkersformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(false, true, "none");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.queryParam("all", "true")
				.queryParam("incident_type", "infrastructure_issue")
				.queryParam("proximity", "perpendicularly")
				.queryParam("occurred_before", "13")
				.queryParam("occurred_after", "94")
				.queryParam("query", "saltine")
				.queryParam("proximity_square", "31")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/locations/markers");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

	@Test
	public void test_s1dx9ykqm9nn_GETversionlocationsmarkersformat() {
		String testResultId = "test_s1dx9ykqm9nn_GETversionlocationsmarkersformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(true, false, "individual_parameter_constraint:Invalid parameter value");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.queryParam("all", "false")
				.queryParam("incident_type", "hazard")
				.queryParam("proximity", "antecedently")
				.queryParam("occurred_after", "15")
				.queryParam("limit", "3")
				.queryParam("proximity_square", "74")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/locations/markers");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

	@Test
	public void test_uvk7neemw8yr_GETversionlocationsmarkersformat() {
		String testResultId = "test_uvk7neemw8yr_GETversionlocationsmarkersformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(false, true, "none");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.queryParam("incident_type", "chop_shop")
				.queryParam("proximity", "languidly")
				.queryParam("occurred_before", "32")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/locations/markers");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

	@Test
	public void test_qhswfu83etsx_GETversionlocationsmarkersformat() {
		String testResultId = "test_qhswfu83etsx_GETversionlocationsmarkersformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(false, true, "none");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.queryParam("incident_type", "unconfirmed")
				.queryParam("occurred_before", "99")
				.queryParam("query", "sneezewort")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/locations/markers");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

	@Test
	public void test_sl0ne13f61pt_GETversionlocationsformat() {
		String testResultId = "test_sl0ne13f61pt_GETversionlocationsformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(false, true, "none");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.queryParam("proximity_square", "35")
				.queryParam("limit", "96")
				.queryParam("incident_type", "infrastructure_issue")
				.queryParam("occurred_after", "60")
				.queryParam("query", "plicate")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/locations");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

	@Test
	public void test_1jeff3y88mp15_GETversionlocationsformat() {
		String testResultId = "test_1jeff3y88mp15_GETversionlocationsformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(false, true, "none");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.queryParam("limit", "65")
				.queryParam("proximity_square", "93")
				.queryParam("incident_type", "chop_shop")
				.queryParam("query", "breathalyse")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/locations");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

	@Test
	public void test_urej19rngbw0_GETversionincidentsidformat() {
		String testResultId = "test_urej19rngbw0_GETversionincidentsidformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(false, true, "none");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.pathParam("id", "83")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/incidents/{id}");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

	@Test
	public void test_vbhfiby230vo_GETversionlocationsformat() {
		String testResultId = "test_vbhfiby230vo_GETversionlocationsformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(true, false, "individual_parameter_constraint:Invalid parameter value");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.queryParam("proximity_square", "false")
				.queryParam("all", "false")
				.queryParam("incident_type", "theft")
				.queryParam("proximity", "formative")
				.queryParam("occurred_before", "47")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/locations");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

	@Test
	public void test_us3hsapxfg4m_GETversionlocationsformat() {
		String testResultId = "test_us3hsapxfg4m_GETversionlocationsformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(false, true, "none");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.queryParam("all", "true")
				.queryParam("incident_type", "crash")
				.queryParam("proximity", "dampening")
				.queryParam("occurred_after", "75")
				.queryParam("query", "stridulate")
				.queryParam("proximity_square", "46")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/locations");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

	@Test
	public void test_1jym2tf8z40ty_GETversionincidentsformat() {
		String testResultId = "test_1jym2tf8z40ty_GETversionincidentsformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(false, true, "none");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.queryParam("incident_type", "infrastructure_issue")
				.queryParam("page", "70")
				.queryParam("occurred_before", "19")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/incidents");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

	@Test
	public void test_trznldw46240_GETversionincidentsformat() {
		String testResultId = "test_trznldw46240_GETversionincidentsformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(false, true, "none");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.queryParam("proximity_square", "78")
				.queryParam("incident_type", "crash")
				.queryParam("occurred_before", "93")
				.queryParam("query", "unappealingly")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/incidents");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

	@Test
	public void test_uv8ne4jvnltg_GETversionincidentsformat() {
		String testResultId = "test_uv8ne4jvnltg_GETversionincidentsformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(false, true, "none");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.queryParam("incident_type", "theft")
				.queryParam("per_page", "24")
				.queryParam("proximity", "broadside")
				.queryParam("occurred_after", "30")
				.queryParam("query", "ostentatiously")
				.queryParam("page", "79")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/incidents");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

	@Test
	public void test_1iyn906z4fak1_GETversionlocationsmarkersformat() {
		String testResultId = "test_1iyn906z4fak1_GETversionlocationsmarkersformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(false, true, "none");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.queryParam("incident_type", "theft")
				.queryParam("occurred_before", "10")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/locations/markers");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

	@Test
	public void test_1ji50mfxy0lko_GETversionincidentsformat() {
		String testResultId = "test_1ji50mfxy0lko_GETversionincidentsformat";

		NominalOrFaultyTestCaseFilter nominalOrFaultyTestCaseFilter = new NominalOrFaultyTestCaseFilter(false, true, "none");

		try {
			Response response = RestAssured
			.given()
				.log().all()
				.queryParam("per_page", "96")
				.queryParam("incident_type", "hazard")
				.queryParam("occurred_before", "84")
				.queryParam("occurred_after", "83")
				.queryParam("proximity_square", "23")
				.filter(new CSVFilter(testResultId, APIName))
				.filter(allureFilter)
				.filter(statusCode5XXFilter)
				.filter(nominalOrFaultyTestCaseFilter)
				.filter(validationFilter)
			.when()
				.get("/v2/incidents");

			response.then().log().all();
			System.out.println("Test passed.");
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
			fail(ex.getMessage());
		}	}

}
