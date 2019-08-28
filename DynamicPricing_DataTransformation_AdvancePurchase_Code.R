#Libraries -------------------------------------------------------------------------------------------------------------------------------------------------------
library(dtplyr)
library(stringr)
library(plyr)
library(bit64)
library(data.table)
library(dplyr)

#Input -------------------------------------------------------------------------------------------------------------------------------------------------------

#Integrated Sales Data (Integrated Sales)
InputIntegratedSales <- fread("F:/Dynamic Pricing/Dynamic Pricing/Data/CD_IS_BOGPSO.txt", header = T, sep = ";")
InputIntegratedSales <- select(InputIntegratedSales, -Indicadores)

#Departure Time Window Data (PROS)
DepartureTimeWindow <- read.csv("F:/DepartureTimeWindow.txt", header = T, sep = ",")

#Holidays and Special Events Data (Demanda)
HolidaysSpecialEvents <- read.csv("F:/HolidaysSpecialEvents.txt", header = T, sep = ";")

#Seat Availability per Advance Purchase Data (VQs)
SeatAvailability <- fread("F:/SeatAvailabilityTotal.csv", header = T, sep = ";")
colnames(SeatAvailability) <- c("OriginDestination", "FlightDate", "FlightHour", "AdvancePurchase", "FlightNumber", "CapacityC", "CapacityY", "BookingsC", "BookingsY", "DisponibilityC", "DisponibilityY")

#SeatsCompetition Data
CompetitionSeats <- read.csv("F:/SeatsLATAM.txt", header = T, sep = ";")
colnames(CompetitionSeats) <- c("FlightDate", "FlightHour", "OriginDestination", "Seats")

#Average Fare Data (Integrated Sales)
AFBOGMDE <- read.csv("F:/AverageFareDomesticMarket (BOGPSO).txt", header = T, sep = ";")
AFBOGMDE$OD <- "BOGPSO"
AFMDEBOG <- read.csv("F:/AverageFareDomesticMarket (PSOBOG).txt", header = T, sep = ";")
AFMDEBOG$OD <- "PSOBOG"
AverageFare <- rbind(AFBOGMDE, AFMDEBOG)
rm(AFBOGMDE, AFMDEBOG)
#AverageFare <- read.csv("F:/AverageFareDomesticMarket.txt", header = T, sep = ";")
colnames(AverageFare) <- c("SalesDate", "FlightDate", "CabinCode", "Passengers", "RevenueUSD", "RevenueCOP", "OD")

#Parameters -------------------------------------------------------------------------------------------------------------------------------------------------------

IntegratedSales <- data.frame()
InicioAnalisis <- "2014-04-01"
FinAnalisis <- "2016-08-31"
#Functions -------------------------------------------------------------------------------------------------------------------------------------------------------

#Funcion que calcula el TOD por OD Local
DepartureTimeWindowFunction <- function(x,y)
{
  y <- subset(y, OD_OW == as.character(unique(x$OriginDestination)))
  for(j in 1:dim(y)[1])
  {
    if(j == 1)
    {
      x$DepartureTimeWindow <- ifelse((x$FlightHour >= y[j,]$BeginTime & x$FlightHour <= y[j,]$EndTime) == "TRUE", as.character(y[j,]$TOD), NA)
    } else x$DepartureTimeWindow <- ifelse( (x$FlightHour >= y[j,]$BeginTime & x$FlightHour <= y[j,]$EndTime) == "TRUE", as.character(y[j,]$TOD), x$DepartureTimeWindow)
  }
  x
}

#Data Transformation -------------------------------------------------------------------------------------------------------------------------------------------------------

#Crea un data frame con nombre "IntegratedSales" y en esta se agregan las metricas y atributos principales. 
IntegratedSales <- data.frame(OriginDestination = InputIntegratedSales$`OD Company OW`)
IntegratedSales$SalesDate <- as.Date(strptime(InputIntegratedSales$`Transaction Date`, "%d/%m/%Y"))
IntegratedSales$FlightDate <- as.Date(strptime(InputIntegratedSales$`OD Company Flight Date`, "%d/%m/%Y"))
IntegratedSales$TicketFare <- InputIntegratedSales$`ISALES - Sales Tracking Fare Amount (COP)`
IntegratedSales$CabinCode <- InputIntegratedSales$`OD Company Bought Cabin Code`
IntegratedSales$FlightNumber <- InputIntegratedSales$`Operating Flight Number as scheduled`
IntegratedSales$FlightDayOfWeek <- weekdays(IntegratedSales$FlightDate)
IntegratedSales$SalesDayOfWeek <- weekdays(IntegratedSales$SalesDate)
IntegratedSales$Demand <- InputIntegratedSales$`ISALES - OD Company Pax`
rm(InputIntegratedSales)

#Calcula el AP
IntegratedSales$AdvancePurchase <- as.numeric(IntegratedSales$FlightDate - IntegratedSales$SalesDate)

#Filtros
IntegratedSales <- subset(IntegratedSales, AdvancePurchase <= 90 & FlightDate >= InicioAnalisis & FlightDate <= FinAnalisis & TicketFare > 0 & CabinCode == "Y")

#Se identifican cada uno de los tipos de fechas 
HolidaysSpecialEvents$Date <- as.Date(HolidaysSpecialEvents$Date)
HolidaysSpecialEvents$Key <- paste(HolidaysSpecialEvents$OD, HolidaysSpecialEvents$Date)
IntegratedSales$Key <- paste(IntegratedSales$OriginDestination, IntegratedSales$FlightDate)
IntegratedSales$HSE <- HolidaysSpecialEvents$HSE[match(IntegratedSales$FlightDate, HolidaysSpecialEvents$Date)]
IntegratedSales$HSE <- ifelse(is.na(IntegratedSales$HSE), "Dia Normal", as.character(IntegratedSales$HSE))
rm(HolidaysSpecialEvents)

#Se obtienes horas de salida y disponibilidad por anticipacion de compra
SeatAvailability$FlightNumber <- as.integer(SeatAvailability$FlightNumber)
SeatAvailability$AdvancePurchase <- as.numeric(as.character(SeatAvailability$AdvancePurchase))
SeatAvailability$FlightDate <- as.Date(strptime(SeatAvailability$FlightDate, "%d/%m/%Y"))
SeatAvailability$Key <- paste(SeatAvailability$OriginDestination, SeatAvailability$FlightDate, SeatAvailability$FlightNumber, SeatAvailability$AdvancePurchase)
SeatAvailability <- select(SeatAvailability, FlightHour, CapacityC, CapacityY, BookingsC, BookingsY, DisponibilityC, DisponibilityY, Key)
IntegratedSales$Key <- paste(IntegratedSales$OriginDestination, IntegratedSales$FlightDate, IntegratedSales$FlightNumber, IntegratedSales$AdvancePurchase)

IntegratedSales <- left_join(IntegratedSales,SeatAvailability, by = "Key") %>% mutate(Availability = ifelse(CabinCode == "C", DisponibilityC, DisponibilityY))
IntegratedSales <- select(IntegratedSales, OriginDestination, SalesDate, FlightDate, SalesDayOfWeek, FlightDayOfWeek, TicketFare, CabinCode, AdvancePurchase, HSE, FlightHour, Availability, Demand)
RegistrosNA <- round(prop.table(table(is.na(IntegratedSales$Availability)))*100,2)
IntegratedSales <- na.omit(IntegratedSales)
IntegratedSales <- IntegratedSales[-which(IntegratedSales$Availability <0),]
IntegratedSales$FlightHour <- strftime(strptime(IntegratedSales$FlightHour, format="%H%M"),"%H:%M")
rm(SeatAvailability)

#Calcula el TOD de cada vuelo
DepartureTimeWindow$BeginTime <- strftime(strptime(DepartureTimeWindow$BeginTime, format="%H:%M"),"%H:%M")
DepartureTimeWindow$EndTime <- strftime(strptime(DepartureTimeWindow$EndTime, format="%H:%M"),"%H:%M")
IntegratedSales <- do.call("rbind",lapply(split(IntegratedSales, IntegratedSales$OriginDestination), DepartureTimeWindowFunction, DepartureTimeWindow))

#Se agrega la informacion a nivel TOD
IntegratedSales$Availability <- as.numeric(IntegratedSales$Availability)
IntegratedSales <- group_by(IntegratedSales, OriginDestination, FlightDate, SalesDate, SalesDayOfWeek, FlightDayOfWeek, CabinCode, AdvancePurchase, HSE,DepartureTimeWindow) %>% summarise(SeatAvailability = sum(Availability), Demand = sum(Demand), Revenue = sum(TicketFare)) %>% mutate(AveragePrice = round_any(Revenue/Demand, 100)) %>% data.frame()

#Obtiene el precio promedio para rutas del mismo length haul
AverageFare$SalesDate <- as.Date(strptime(AverageFare$SalesDate, "%d/%m/%Y"))
AverageFare$FlightDate <- as.Date(strptime(AverageFare$FlightDate, "%d/%m/%Y"))
AverageFare$AdvancePurchase <- as.numeric(AverageFare$FlightDate - AverageFare$SalesDate)
AverageFare$AdvancePurchaseWeek <- ceiling(AverageFare$AdvancePurchase/7)
AverageFare$AdvancePurchaseWeek <- ifelse(AverageFare$AdvancePurchaseWeek == 0, 1, AverageFare$AdvancePurchaseWeek)
AverageFare$RevenueCOP <- as.numeric(AverageFare$RevenueCOP)
AverageFare <- group_by(AverageFare, CabinCode, FlightDate, AdvancePurchase, OD) %>% summarise(Demand = sum(Passengers), Revenue = sum(RevenueCOP)) %>% mutate(AverageMarketPrice = round_any(Revenue/Demand, 100)) %>% data.frame()
AverageFare <- subset(AverageFare, Demand != 0)
AverageFare$Key <- paste(AverageFare$AdvancePurchase, AverageFare$FlightDate, AverageFare$CabinCode, AverageFare$OD)
IntegratedSales$Key <- paste(IntegratedSales$AdvancePurchase, IntegratedSales$FlightDate, IntegratedSales$CabinCode, IntegratedSales$OriginDestination)
IntegratedSales$AverageMarketPrice <- AverageFare$AverageMarketPrice[match(IntegratedSales$Key, AverageFare$Key)]
rm(AverageFare)

#Obtiene las sillas de la competencia
CompetitionSeats <- subset(CompetitionSeats, OriginDestination %in% as.vector(unique(IntegratedSales$OriginDestination)))
CompetitionSeats$FlightHour <- strftime(strptime(CompetitionSeats$FlightHour, format="%H:%M"),"%H:%M")
CompetitionSeats <- do.call("rbind",lapply(split(CompetitionSeats, CompetitionSeats$OriginDestination), DepartureTimeWindowFunction, DepartureTimeWindow))
CompetitionSeats <-  group_by(CompetitionSeats, OriginDestination, FlightDate, DepartureTimeWindow) %>% summarise(Seats = sum(Seats)) %>% data.frame()
CompetitionSeats$FlightDate <- as.Date(strptime(CompetitionSeats$FlightDate ,"%d/%m/%Y"))
CompetitionSeats$Key <- paste(CompetitionSeats$OriginDestination, CompetitionSeats$FlightDate, CompetitionSeats$DepartureTimeWindow)
IntegratedSales$Key <- paste(IntegratedSales$OriginDestination, IntegratedSales$FlightDate, IntegratedSales$DepartureTimeWindow)
IntegratedSales$CompetitionSeats <- CompetitionSeats$Seats[match(IntegratedSales$Key, CompetitionSeats$Key)]
IntegratedSales$CompetitionSeats <- as.numeric(as.character(ifelse(is.na(IntegratedSales$CompetitionSeats),as.character(0),IntegratedSales$CompetitionSeats)))
rm(DepartureTimeWindow, CompetitionSeats, DepartureTimeWindowFunction)

ConsolidadoIntegratedSales <- select(IntegratedSales, OriginDestination, FlightDate, SalesDayOfWeek, FlightDayOfWeek, CabinCode, HSE, AdvancePurchase, DepartureTimeWindow, Demand, AveragePrice, SeatAvailability, AverageMarketPrice, CompetitionSeats)
ConsolidadoIntegratedSales <- na.omit(ConsolidadoIntegratedSales)
rm(IntegratedSales)


#Output -------------------------------------------------------------------------------------------------------------------------------------------------------
#Exporta la informacion 
LegRoundTrip <- as.character(unique(ConsolidadoIntegratedSales$OriginDestination)[1])
write.table(ConsolidadoIntegratedSales, paste("F:/Dynamic Pricing/Dynamic Pricing/Data/Output_", LegRoundTrip, "RT_V2.csv", sep = ""), row.names = FALSE, sep = ";", quote = FALSE)



