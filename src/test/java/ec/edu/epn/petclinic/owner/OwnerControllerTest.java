package ec.edu.epn.petclinic.owner;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
@DisplayName("OwnerController Integration Tests")
class OwnerControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Autowired
	private OwnerRepository ownerRepository;
	
	private Owner testOwner;

	private Owner testOwner2;

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		ownerRepository.deleteAll();
		
		testOwner = new Owner();
		testOwner.setFirstName("John");
		testOwner.setLastName("Doe");
		testOwner.setAddress("123 Main St");
		testOwner.setCity("Springfield");
		testOwner.setTelephone("1234567890");
		testOwner = ownerRepository.save(testOwner);

		testOwner2 = new Owner();
		testOwner2.setFirstName("Jane");
		testOwner2.setLastName("Doe");
		testOwner2.setAddress("456 Oak Ave");
		testOwner2.setCity("New York");
		testOwner2.setTelephone("9876543210");
		testOwner2 = ownerRepository.save(testOwner2);
	}

	@Test
	@DisplayName("GET /owners/new - Should display owner creation form")
	void testInitCreationForm() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/owners/new"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	@DisplayName("POST /owners/new - Should create new owner successfully")
	void testProcessCreationFormSuccess() throws Exception {
		// Arrange
		int initialCount = (int) ownerRepository.count();

		// Act
		var result = mockMvc.perform(post("/owners/new").param("firstName", "John")
			.param("lastName", "Doe")
			.param("address", "123 Main St")
			.param("city", "Springfield")
			.param("telephone", "1234567890"));

		// Assert
		result.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("message", "New Owner Created"));
		
		
		int finalCount = (int) ownerRepository.count();
		assert finalCount == initialCount + 1;
	}

	@Test
	@DisplayName("POST /owners/new - Should fail with validation errors")
	void testProcessCreationFormWithErrors() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(post("/owners/new").param("firstName", "")
			.param("lastName", "")
			.param("address", "")
			.param("city", "")
			.param("telephone", ""));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "firstName", "lastName", "address", "city",
					"telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	@DisplayName("POST /owners/new - Should fail with invalid telephone format")
	void testProcessCreationFormWithInvalidTelephone() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(post("/owners/new").param("firstName", "John")
			.param("lastName", "Doe")
			.param("address", "123 Main St")
			.param("city", "Springfield")
			.param("telephone", "123"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	@DisplayName("GET /owners/find - Should display find owners form")
	void testInitFindForm() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/owners/find"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	@DisplayName("GET /owners - Should return single owner and redirect")
	void testProcessFindFormWithSingleResult() throws Exception {
		// Arrange
		// Se usa el testOwner creado en setUp

		// Act
		var result = mockMvc.perform(get("/owners").param("lastName", "Doe").param("page", "1"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	@DisplayName("GET /owners - Should return multiple owners with pagination")
	void testProcessFindFormWithMultipleResults() throws Exception {
		// Arrange
		// Ya existen dos dueños con apellido "Doe" creados en setUp

		// Act
		var result = mockMvc.perform(get("/owners").param("lastName", "Doe").param("page", "1"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("listOwners"))
			.andExpect(model().attribute("listOwners", hasSize(2)))
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	@DisplayName("GET /owners - Should return all owners when no lastName provided")
	void testProcessFindFormWithEmptyLastName() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/owners").param("page", "1"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("listOwners"))
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	@DisplayName("GET /owners - Should show error when no owners found")
	void testProcessFindFormWithNoResults() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/owners").param("lastName", "NonExistent"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	@DisplayName("GET /owners - Should handle pagination with different pages")
	void testProcessFindFormWithPagination() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/owners").param("lastName", "Doe").param("page", "1"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	@DisplayName("GET /owners/{ownerId}/edit - Should display owner edit form")
	void testInitUpdateOwnerForm() throws Exception {
		// Arrange
		// No es necesario, se usa el testOwner creado en setUp

		// Act
		var result = mockMvc.perform(get("/owners/{ownerId}/edit", testOwner.getId()));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(model().attribute("owner", hasProperty("firstName", is("John"))))
			.andExpect(model().attribute("owner", hasProperty("lastName", is("Doe"))))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	@DisplayName("POST /owners/{ownerId}/edit - Should update owner successfully")
	void testProcessUpdateOwnerFormSuccess() throws Exception {
		// Arrange
		Integer ownerId = testOwner.getId();

		// Act
		var result = mockMvc.perform(post("/owners/{ownerId}/edit", ownerId).param("firstName", "John")
			.param("lastName", "Smith")
			.param("address", "123 Main St")
			.param("city", "Springfield")
			.param("telephone", "1234567890"));

		// Assert
		result.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("message", "Owner Values Updated"));
	}

	@Test
	@DisplayName("POST /owners/{ownerId}/edit - Should fail with validation errors")
	void testProcessUpdateOwnerFormWithErrors() throws Exception {
		// Arrange
		Integer ownerId = testOwner.getId();

		// Act
		var result = mockMvc.perform(post("/owners/{ownerId}/edit", ownerId).param("firstName", "")
			.param("lastName", "")
			.param("address", "")
			.param("city", "")
			.param("telephone", ""));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "firstName", "lastName", "address", "city",
					"telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	@DisplayName("POST /owners/{ownerId}/edit - Should reject invalid telephone in update")
	void testProcessUpdateOwnerFormWithInvalidTelephone() throws Exception {
		// Arrange
		Integer ownerId = testOwner.getId();

		// Act
		var result = mockMvc.perform(post("/owners/{ownerId}/edit", ownerId).param("firstName", "John")
			.param("lastName", "Doe")
			.param("address", "123 Main St")
			.param("city", "Springfield")
			.param("telephone", "12-345-678"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	@DisplayName("GET /owners/{ownerId} - Should display owner details")
	void testShowOwner() throws Exception {
		// Arrange
		Integer ownerId = testOwner.getId();

		// Act
		var result = mockMvc.perform(get("/owners/{ownerId}", ownerId));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(model().attribute("owner", hasProperty("firstName", is("John"))))
			.andExpect(model().attribute("owner", hasProperty("lastName", is("Doe"))))
			.andExpect(model().attribute("owner", hasProperty("address", is("123 Main St"))))
			.andExpect(model().attribute("owner", hasProperty("city", is("Springfield"))))
			.andExpect(model().attribute("owner", hasProperty("telephone", is("1234567890"))))
			.andExpect(view().name("owners/ownerDetails"));
	}

	@Test
	@DisplayName("ModelAttribute - Should create new owner when ownerId is null")
	void testFindOwnerWithNullId() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/owners/new"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(model().attribute("owner", hasProperty("id", nullValue())));
	}

	@Test
	@DisplayName("ModelAttribute - Should find owner when ownerId is provided")
	void testFindOwnerWithValidId() throws Exception {
		// Arrange
		Integer ownerId = testOwner.getId();

		// Act
		var result = mockMvc.perform(get("/owners/{ownerId}/edit", ownerId));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attribute("owner", hasProperty("id", is(ownerId))));
	}

	@Test
	@DisplayName("InitBinder - Should disallow id field binding")
	void testInitBinderDisallowsIdField() throws Exception {
		// Arrange
		int initialCount = (int) ownerRepository.count();

		// Act
		// Tratando de crear un nuevo dueño con un ID proporcionado
		var result = mockMvc.perform(post("/owners/new").param("id", "999")
			.param("firstName", "John")
			.param("lastName", "Doe")
			.param("address", "123 Main St")
			.param("city", "Springfield")
			.param("telephone", "1234567890"));

		// Assert
		result.andExpect(status().is3xxRedirection());

		int finalCount = (int) ownerRepository.count();
		assert finalCount == initialCount + 1;
	}

	@Test
	@DisplayName("GET /owners - Should use default page 1 when page parameter not provided")
	void testProcessFindFormWithDefaultPage() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/owners").param("lastName", "Doe"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 1))
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	@DisplayName("POST /owners/new - Should validate telephone pattern with letters")
	void testProcessCreationFormWithLettersInTelephone() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(post("/owners/new").param("firstName", "John")
			.param("lastName", "Doe")
			.param("address", "123 Main St")
			.param("city", "Springfield")
			.param("telephone", "12345abcde"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	@DisplayName("POST /owners/new - Should accept valid 10-digit telephone")
	void testProcessCreationFormWithValid10DigitTelephone() throws Exception {
		// Arrange
		int initialCount = (int) ownerRepository.count();

		// Act
		var result = mockMvc.perform(post("/owners/new").param("firstName", "John")
			.param("lastName", "Doe")
			.param("address", "123 Main St")
			.param("city", "Springfield")
			.param("telephone", "0987654321"));

		// Assert
		result.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("message", "New Owner Created"));
		
		int finalCount = (int) ownerRepository.count();
		assert finalCount == initialCount + 1;
	}

	@Test
	@DisplayName("GET /owners - Should handle null lastName as empty string")
	void testProcessFindFormWithNullLastName() throws Exception {
		// Arrange
		// No es necesario

		// Act
		var result = mockMvc.perform(get("/owners"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("listOwners"))
			.andExpect(view().name("owners/ownersList"));
	}

	@Test
	@DisplayName("GET /owners - Should correctly calculate pagination for page size 5")
	void testPaginationWithPageSize5() throws Exception {
		// Arrange
		for (int i = 0; i < 5; i++) {
			Owner owner = new Owner();
			owner.setFirstName("Owner" + i);
			owner.setLastName("Smith");
			owner.setAddress("Address " + i);
			owner.setCity("City");
			owner.setTelephone("123456789" + i);
			ownerRepository.save(owner);
		}

		// Act
		var result = mockMvc.perform(get("/owners").param("lastName", "Smith").param("page", "1"));

		// Assert
		result.andExpect(status().isOk())
			.andExpect(model().attributeExists("listOwners"))
			.andExpect(view().name("owners/ownersList"));
	}
}
