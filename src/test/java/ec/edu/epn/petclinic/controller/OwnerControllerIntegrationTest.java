package ec.edu.epn.petclinic.controller;

@SpringBootTest
@AutoConfigureMockMvc
class OwnerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateOwner() throws Exception {
        mockMvc.perform(post("/owners/new")
                .param("firstName", "Ana")
                .param("lastName", "Lopez")
                .param("address", "Av Central")
                .param("city", "Quito")
                .param("telephone", "0988888888"))
            .andExpect(status().is3xxRedirection());
    }
}
