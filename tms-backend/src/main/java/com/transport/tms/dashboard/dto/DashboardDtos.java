package com.transport.tms.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MdDashboardDto {
        private BigDecimal monthlyGrossRevenue;
        private BigDecimal monthlyOperatingProfit;
        private BigDecimal totalReceivables;
        private BigDecimal totalBankCashPosition;
        private BigDecimal totalDieselExpense;
        private BigDecimal totalMaintenanceExpense;
        private long totalTripsCount;
        private long activeVehiclesCount;
        private long totalVehiclesCount;
        private double fleetUtilizationRate;
        private long pendingApprovalsCount;
        private List<TruckProfitabilityDto> truckProfitability;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TruckProfitabilityDto {
        private String vehicleRegistration;
        private String ownership;
        private long tripsCount;
        private BigDecimal grossRevenue;
        private BigDecimal dieselCost;
        private BigDecimal maintenanceCost;
        private BigDecimal netMargin;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountsDashboardDto {
        private long unbilledTripsCount;
        private BigDecimal totalOutstandingReceivables;
        private BigDecimal cashInHandBalance;
        private BigDecimal corporateBankBalance;
        private BigDecimal totalMonthlyCollections;
        private BigDecimal totalPendingDriverAdvances;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ManagerDashboardDto {
        private long availableVehiclesCount;
        private long onTripVehiclesCount;
        private long maintenanceVehiclesCount;
        private long availableDriversCount;
        private long todayTripsCount;
        private BigDecimal todayTonnageDispatched;
    }
}
