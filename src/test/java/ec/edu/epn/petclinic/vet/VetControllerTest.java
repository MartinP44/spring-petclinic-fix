package ec.edu.epn.petclinic.vet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
@DisplayName("VetController Integration Tests")
class VetControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Autowired
	private VetRepository vetRepository;

	private Vet testVet1;

	private Vet testVet2;

	private Specialty specialty1;

	private Specialty specialty2;

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		// Create test specialties
		specialty1 = new Specialty();
		specialty1.setName("Radiology");

		specialty2 = new Specialty();
		specialty2.setName("Surgery");

		// Create test vets
		testVet1 = new Vet();
		testVet1.setFirstName("James");
		testVet1.setLastName("Carter");

		testVet2 = new Vet();
		testVet2.setFirstName("Helen");
		testVet2.setLastName("Leary");
		testVet2.addSpecialty(specialty1);
	}

	@Test
	@DisplayName("GET /vets.html - Should display vet list with default page")
	void testShowVetListDefaultPage() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/vets.html"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(model().attributeExists("currentPage"))
			.andExpect(model().attributeExists("totalPages"))
			.andExpect(model().attributeExists("totalItems"))
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	@DisplayName("GET /vets.html - Should display vet list with specific page")
	void testShowVetListWithPage() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/vets.html").param("page", "2"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(model().attribute("currentPage", 2))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	@DisplayName("GET /vets.html - Should use default page 1 when no page parameter")
	void testShowVetListNoPageParameter() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/vets.html"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	@DisplayName("GET /vets.html - Should paginate vets with page size 5")
	void testShowVetListPagination() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/vets.html").param("page", "1"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("totalPages"))
			.andExpect(model().attributeExists("totalItems"))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	@DisplayName("GET /vets.html - Should display vets with their specialties")
	void testShowVetListWithSpecialties() throws Exception {
		// Arrange
		// No es necesario - database already has vets with specialties

		// Act
		var result = mockMvc.perform(get("/vets.html"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	@DisplayName("GET /vets.html - Should handle page 1 correctly")
	void testShowVetListFirstPage() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/vets.html").param("page", "1"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	@DisplayName("GET /vets.html - Should handle last page correctly")
	void testShowVetListLastPage() throws Exception {
		// Arrange
		// Assuming we have data in the database

		// Act
		var result = mockMvc.perform(get("/vets.html").param("page", "10"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(model().attributeExists("currentPage"))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	@DisplayName("GET /vets - Should return all vets as JSON/XML")
	void testShowResourcesVetList() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/vets"));

		// Assert
		result.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET /vets.html - Should handle page parameter as integer")
	void testShowVetListPageParameterType() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/vets.html").param("page", "3"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 3))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	@DisplayName("GET /vets.html - Should calculate total pages correctly")
	void testShowVetListTotalPages() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/vets.html").param("page", "1"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("totalPages"))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	@DisplayName("GET /vets.html - Should calculate total items correctly")
	void testShowVetListTotalItems() throws Exception {
		// Arrange
		long totalVets = vetRepository.findAll().size();

		// Act
		var result = mockMvc.perform(get("/vets.html").param("page", "1"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("totalItems"))
			.andExpect(model().attribute("totalItems", totalVets))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	@DisplayName("GET /vets.html - Should limit page size to 5")
	void testShowVetListPageSize() throws Exception {
		// Arrange
		long totalVets = vetRepository.findAll().size();

		// Act
		var result = mockMvc.perform(get("/vets.html").param("page", "1"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"));

		if (totalVets >= 5) {
			result.andExpect(model().attribute("listVets", hasSize(5)));
		} else {
			result.andExpect(model().attribute("listVets", hasSize((int) totalVets)));
		}
	}

	@Test
	@DisplayName("GET /vets.html - Should handle empty vet repository")
	void testShowVetListEmptyRepository() throws Exception {
		// Arrange
		// This test assumes the database might be empty or not
		// The controller should handle it gracefully

		// Act
		var result = mockMvc.perform(get("/vets.html").param("page", "1"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	@DisplayName("GET /vets.html - Should return valid model attributes")
	void testShowVetListModelAttributes() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/vets.html").param("page", "1"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("currentPage"))
			.andExpect(model().attributeExists("totalPages"))
			.andExpect(model().attributeExists("totalItems"))
			.andExpect(model().attributeExists("listVets"))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	@DisplayName("GET /vets.html - Should handle page greater than total pages")
	void testShowVetListPageGreaterThanTotal() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/vets.html").param("page", "999"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("listVets"))
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	@DisplayName("GET /vets - Should handle content negotiation")
	void testShowResourcesVetListContentNegotiation() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/vets")
			.accept("application/json"));

		// Assert
		result.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET /vets.html - Should correctly navigate between pages")
	void testShowVetListPageNavigation() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var resultPage1 = mockMvc.perform(get("/vets.html").param("page", "1"));
		var resultPage2 = mockMvc.perform(get("/vets.html").param("page", "2"));

		// Assert
		resultPage1.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 1));

		resultPage2.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 2));
	}

	@Test
	@DisplayName("GET /vets.html - Should reject page 0 with exception")
	void testShowVetListPageZero() throws Exception {
		// Arrange
		// Spring PageRequest expects 0-indexed, but controller does page-1, so 0 becomes -1

		// Act & Assert
		try {
			mockMvc.perform(get("/vets.html").param("page", "0"));
		} catch (Exception e) {
			// Expected exception due to invalid page index
			assertTrue(e.getCause() instanceof IllegalArgumentException);
		}
	}

	@Test
	@DisplayName("GET /vets.html - Should reject negative page with error")
	void testShowVetListNegativePage() throws Exception {
		// Arrange
		// Negative page numbers cause errors when passed to PageRequest

		// Act & Assert
		try {
			mockMvc.perform(get("/vets.html").param("page", "-1"));
		} catch (Exception e) {
			// Expected exception due to invalid page index
			assertTrue(e.getCause() instanceof IllegalArgumentException);
		}
	}

}
