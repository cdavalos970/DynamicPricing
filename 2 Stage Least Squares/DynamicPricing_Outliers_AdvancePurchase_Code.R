#Libraries -----------------------------------------------------------------------------------------------------------------------------------------------------
library(data.table)
library(plyr)
library(outliers)
library(mvoutlier)
#Input ---------------------------------------------------------------------------------------------------------------------------------------------------------

#Integrated Sales Data (Integrated Sales)
Input <- fread("F:/Dynamic Pricing/Dynamic Pricing/Data/Output_BOGPSORT_V2.csv", header = T, sep = ";")
Input <- subset(Input, HSE == "Dia Normal")
#Funciones -----------------------------------------------------------------------------------------------------------------------------------------------------

OutlierFunction <- function(x)
{
  x$SeatAvailability <- ifelse(x$SeatAvailability == 0, x$Demand, x$SeatAvailability)
  Leg <<- unique( paste(x$OriginDestination, x$CabinCode, x$HSE, x$AdvancePurchase, x$DepartureTimeWindow))
  LogOutlierVariables <- data.frame(AveragePrice = log(x$AveragePrice), SeatAvailability = log(x$SeatAvailability))
  y <- try(aq.plot(LogOutlierVariables, alpha=0.03)$outliers, silent = TRUE)
  
  if(dim(x)[1] > 3 & class(y) != "try-error")
  {
    x$Outlier <- y
    return(x)
  }
  
}

#Proceso -------------------------------------------------------------------------------------------------------------------------------------------------------
#Divide la informacion de acuerdo al nivel de agregacion definido
SPL <- split(Input, paste(Input$OriginDestination, Input$CabinCode, Input$HSE, Input$AdvancePurchase, Input$DepartureTimeWindow))
#Aplica a cada subset de datos la funcion y une todo en un mismo conjunto de datos
Output <- do.call("rbind", lapply(SPL, OutlierFunction))

#Output -------------------------------------------------------------------------------------------------------------------------------------------------------
LegRoundTrip <- as.character(unique(Output$OriginDestination)[1])
write.table(Output, paste("F:/Dynamic Pricing/Dynamic Pricing/Data/OutlierOutput_", LegRoundTrip, "RT_V2.csv", sep = ""), row.names = FALSE, sep = ";", quote = FALSE)
