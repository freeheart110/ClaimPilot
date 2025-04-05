package com.bruceyulin.claimpilot.service;

import com.bruceyulin.claimpilot.model.Claim;
import com.bruceyulin.claimpilot.model.ClaimStatus;
import com.bruceyulin.claimpilot.model.ClaimType;
import com.bruceyulin.claimpilot.model.PolicyHolder;
import com.bruceyulin.claimpilot.repository.ClaimRepository;
import com.bruceyulin.claimpilot.dto.ClaimDTO;
import com.bruceyulin.claimpilot.dto.PolicyHolderDTO;
import com.bruceyulin.claimpilot.mapper.ClaimMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClaimServiceTest {

  @Mock
  private ClaimRepository claimRepository;

  @Mock
  private PolicyHolderService policyHolderService;

  @InjectMocks
  private ClaimService claimService;

  private Claim claim;
  private ClaimDTO claimDTO;
  private PolicyHolder policyHolder;
  private PolicyHolderDTO policyHolderDTO;

  @BeforeEach
  public void setUp() {
    policyHolder = PolicyHolder.builder()
        .id(1L)
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com")
        .phone("1234567890")
        .address("123 Street")
        .city("City")
        .province("Province")
        .postalCode("A1B2C3")
        .driverLicenseNumber("License123")
        .vehicleVIN("VIN123")
        .build();

    policyHolderDTO = PolicyHolderDTO.builder()
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com")
        .phone("1234567890")
        .address("123 Street")
        .city("City")
        .province("Province")
        .postalCode("A1B2C3")
        .driverLicenseNumber("License123")
        .vehicleVIN("VIN123")
        .build();

    claim = Claim.builder()
        .id(1L)
        .claimNumber("CLM-20230101-000001")
        .claimType(ClaimType.COLLISION)
        .status(ClaimStatus.SUBMITTED)
        .claimDate(LocalDate.now())
        .dateOfAccident(LocalDate.now().minusDays(1))
        .accidentDescription("Rear-end collision")
        .policeReportNumber("PR123")
        .locationOfAccident("Highway 1")
        .damageDescription("Rear bumper damage")
        .estimatedRepairCost(BigDecimal.valueOf(1000))
        .finalSettlementAmount(BigDecimal.valueOf(900))
        .policyHolder(policyHolder)
        .build();

    claimDTO = ClaimDTO.builder()
        .id(1L)
        .claimType(ClaimType.COLLISION)
        .dateOfAccident(LocalDate.now().minusDays(1))
        .accidentDescription("Rear-end collision")
        .policeReportNumber("PR123")
        .locationOfAccident("Highway 1")
        .damageDescription("Rear bumper damage")
        .estimatedRepairCost(BigDecimal.valueOf(1000))
        .finalSettlementAmount(BigDecimal.valueOf(900))
        .policyHolder(policyHolderDTO)
        .build();
  }

  @Test
  public void testGetAllClaims() {
    when(claimRepository.findAll()).thenReturn(List.of(claim));
    List<Claim> claims = claimService.getAllClaims();
    assertEquals(1, claims.size());
    assertEquals(claim, claims.get(0));
  }

  @Test
  public void testGetClaimById_Found() {
    when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
    Claim foundClaim = claimService.getClaimById(1L);
    assertNotNull(foundClaim);
    assertEquals(claim, foundClaim);
  }

  @Test
  public void testGetClaimById_NotFound() {
    when(claimRepository.findById(1L)).thenReturn(Optional.empty());
    Claim foundClaim = claimService.getClaimById(1L);
    assertNull(foundClaim);
  }

  @Test
  public void testDeleteClaim() {
    claimService.deleteClaim(1L);
    verify(claimRepository, times(1)).deleteById(1L);
  }

  @Test
  public void testSubmitClaim() {
    // Arrange
    when(policyHolderService.findOrCreate(anyString(), anyString(), anyString(), anyString(),
        anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
        .thenReturn(policyHolder);

    // Mock the static method using mockStatic
    try (MockedStatic<ClaimMapper> mockedStatic = Mockito.mockStatic(ClaimMapper.class)) {
      mockedStatic.when(() -> ClaimMapper.toEntity(any(ClaimDTO.class))).thenReturn(claim);

      when(claimRepository.save(any(Claim.class))).thenReturn(claim);

      // Act
      Claim savedClaim = claimService.submitClaim(claimDTO);

      // Assert
      assertNotNull(savedClaim);
      assertEquals(ClaimStatus.SUBMITTED, savedClaim.getStatus());
      assertNotNull(savedClaim.getClaimNumber());
      assertTrue(savedClaim.getClaimNumber().startsWith("CLM-"));
      assertEquals(policyHolder, savedClaim.getPolicyHolder());
      verify(claimRepository, times(1)).save(any(Claim.class));
    }
  }

  @Test
  public void testGetClaimStatusFlexible_ValidInput_ClaimNumberAndEmail() {
    when(claimRepository.findAll()).thenReturn(List.of(claim));
    String status = claimService.getClaimStatusFlexible("CLM-20230101-000001", "john.doe@example.com", null, null);
    assertEquals("SUBMITTED", status);
  }

  @Test
  public void testGetClaimStatusFlexible_ValidInput_NameAndEmail() {
    when(claimRepository.findAll()).thenReturn(List.of(claim));
    String status = claimService.getClaimStatusFlexible(null, "john.doe@example.com", "John", "Doe");
    assertEquals("SUBMITTED", status);
  }

  @Test
  public void testGetClaimStatusFlexible_InsufficientIdentifiers() {
    assertThrows(IllegalArgumentException.class, () -> {
      claimService.getClaimStatusFlexible("CLM-20230101-000001", null, null, null);
    });
  }

  @Test
  public void testGetClaimStatusFlexible_NoMatchingClaim() {
    when(claimRepository.findAll()).thenReturn(List.of());
    assertThrows(RuntimeException.class, () -> {
      claimService.getClaimStatusFlexible("CLM-20230101-000001", "john.doe@example.com", "John", "Doe");
    });
  }

  @Test
  public void testUpdateClaim_ClaimFieldsOnly() {
    when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
    when(claimRepository.save(any(Claim.class))).thenReturn(claim);

    ClaimDTO updatedClaimDto = ClaimDTO.builder()
        .status(ClaimStatus.IN_REVIEW)
        .estimatedRepairCost(BigDecimal.valueOf(1500))
        .build();

    Claim updatedClaim = claimService.updateClaim(1L, updatedClaimDto);

    assertEquals(ClaimStatus.IN_REVIEW, updatedClaim.getStatus());
    assertEquals(BigDecimal.valueOf(1500), updatedClaim.getEstimatedRepairCost());
    verify(claimRepository, times(1)).save(claim);
  }

  @Test
  public void testUpdateClaim_WithPolicyHolder() {
    when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
    when(claimRepository.save(any(Claim.class))).thenReturn(claim);

    PolicyHolderDTO updatedPhDto = PolicyHolderDTO.builder()
        .firstName("Jane")
        .email("jane.doe@example.com")
        .build();
    ClaimDTO updatedClaimDto = ClaimDTO.builder()
        .status(ClaimStatus.APPROVED)
        .policyHolder(updatedPhDto)
        .build();

    Claim updatedClaim = claimService.updateClaim(1L, updatedClaimDto);

    assertEquals(ClaimStatus.APPROVED, updatedClaim.getStatus());
    assertEquals("Jane", updatedClaim.getPolicyHolder().getFirstName());
    assertEquals("jane.doe@example.com", updatedClaim.getPolicyHolder().getEmail());
    assertEquals("Doe", updatedClaim.getPolicyHolder().getLastName()); // Unchanged field
    verify(claimRepository, times(1)).save(claim);
  }

  @Test
  public void testUpdateClaim_ClaimNotFound() {
    when(claimRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> {
      claimService.updateClaim(1L, new ClaimDTO());
    });
  }
}