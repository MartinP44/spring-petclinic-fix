package ec.edu.epn.petclinic.service;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    @Test
    void shouldSaveOwnerSuccessfully() {
        Owner owner = new Owner();
        owner.setFirstName("Juan");
        owner.setLastName("Perez");

        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);

        Owner savedOwner = ownerService.save(owner);

        assertNotNull(savedOwner);
        verify(ownerRepository, times(1)).save(owner);
    }
}
