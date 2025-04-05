package com.bruceyulin.claimpilot.service;

import com.bruceyulin.claimpilot.model.PolicyHolder;
import com.bruceyulin.claimpilot.repository.PolicyHolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PolicyHolderServiceTest {

  @Mock
  private PolicyHolderRepository policyHolderRepository;

  @InjectMocks
  private PolicyHolderService policyHolderService;

  private PolicyHolder policyHolder;

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
  }

  @Test
  public void testGetAllPolicyHolders() {
    when(policyHolderRepository.findAll()).thenReturn(List.of(policyHolder));
    List<PolicyHolder> policyHolders = policyHolderService.getAllPolicyHolders();
    assertEquals(1, policyHolders.size());
    assertEquals(policyHolder, policyHolders.get(0));
  }

  @Test
  public void testSavePolicyHolder() {
    when(policyHolderRepository.save(any(PolicyHolder.class))).thenReturn(policyHolder);
    PolicyHolder savedPolicyHolder = policyHolderService.savePolicyHolder(policyHolder);
    assertEquals(policyHolder, savedPolicyHolder);
    verify(policyHolderRepository, times(1)).save(policyHolder);
  }

  @Test
  public void testGetPolicyHolderById_Found() {
    when(policyHolderRepository.findById(1L)).thenReturn(Optional.of(policyHolder));
    PolicyHolder foundPolicyHolder = policyHolderService.getPolicyHolderById(1L);
    assertNotNull(foundPolicyHolder);
    assertEquals(policyHolder, foundPolicyHolder);
  }

  @Test
  public void testGetPolicyHolderById_NotFound() {
    when(policyHolderRepository.findById(1L)).thenReturn(Optional.empty());
    PolicyHolder foundPolicyHolder = policyHolderService.getPolicyHolderById(1L);
    assertNull(foundPolicyHolder);
  }

  @Test
  public void testDeletePolicyHolder() {
    policyHolderService.deletePolicyHolder(1L);
    verify(policyHolderRepository, times(1)).deleteById(1L);
  }

  @Test
  public void testFindOrCreate_ExistingPolicyHolder() {
    when(policyHolderRepository.findByFirstNameAndLastNameAndEmailAndPhone(
        "John", "Doe", "john.doe@example.com", "1234567890"))
        .thenReturn(Optional.of(policyHolder));

    PolicyHolder foundPolicyHolder = policyHolderService.findOrCreate(
        "John", "Doe", "john.doe@example.com", "1234567890",
        "123 Street", "City", "Province", "A1B2C3", "License123", "VIN123");

    assertEquals(policyHolder, foundPolicyHolder);
    verify(policyHolderRepository, never()).save(any(PolicyHolder.class));
  }

  @Test
  public void testFindOrCreate_NewPolicyHolder() {
    when(policyHolderRepository.findByFirstNameAndLastNameAndEmailAndPhone(
        "John", "Doe", "john.doe@example.com", "1234567890"))
        .thenReturn(Optional.empty());
    when(policyHolderRepository.save(any(PolicyHolder.class))).thenReturn(policyHolder);

    PolicyHolder newPolicyHolder = policyHolderService.findOrCreate(
        "John", "Doe", "john.doe@example.com", "1234567890",
        "123 Street", "City", "Province", "A1B2C3", "License123", "VIN123");

    assertEquals(policyHolder, newPolicyHolder);
    verify(policyHolderRepository, times(1)).save(any(PolicyHolder.class));
  }
}