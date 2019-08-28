#Libraries -----------------------------------------------------------------------------------------------------------------------------------------------------
library(data.table)
library(AER)
library(ggplot2)
library(plyr)
library(scales)
library(dplyr)
#Input ---------------------------------------------------------------------------------------------------------------------------------------------------------

#Integrated Sales Data (Integrated Sales)
Input <- fread("F:/Dynamic Pricing/Dynamic Pricing/Data/OutlierOutput_BOGMDERT.csv", header = T, sep = ";")
#Funciones -----------------------------------------------------------------------------------------------------------------------------------------------------

TwoSLSFunction <- function(x)
{
  x$AdvancePurchase <- as.factor(x$AdvancePurchase)
  x <- subset(x, Outlier == FALSE)
  fitols <- lm(log(Demand) ~ AveragePrice + DepartureTimeWindow + AdvancePurchase + FlightDayOfWeek + SalesDayOfWeek, data = x)
  #fit2sls <- ivreg(log(Demand) ~ AveragePrice + DepartureTimeWindow + AdvancePurchase + FlightDayOfWeek + SalesDayOfWeek|. - AveragePrice + AverageMarketPrice, data = x)
  #summary(fitols)
  summary(fit2sls)
  
  DataFit2sls <- data.frame(Residuals = fit2sls$residuals, FittedValues = fit2sls$fitted.values, StdResiduals = scale(fit2sls$residuals))
  q <- qqnorm(DataFit2sls$Residuals , plot = FALSE)
  DataFit2sls <- cbind(DataFit2sls, data.frame(Theoretical = q$x))
  
  ggplot(DataFit2sls, aes(FittedValues, Residuals)) +
    geom_point(na.rm = TRUE) +
    geom_hline(yintercept=0, col="red", linetype="dashed") + 
    labs(x = "Fitted Values", y = "Residuals") + 
    ggtitle("Residual vs Fitted Plot") + 
    theme_bw()
  
    ggplot(DataFit2sls, aes(qqnorm(StdResiduals)[[1]], StdResiduals)) + 
    geom_point(na.rm = TRUE) + 
    geom_abline(aes(qqline(StdResiduals))) + 
    xlab("Theoretical Quantiles") + ylab("Standardized Residuals") + 
      ggtitle("Normal Q-Q") + 
      theme_bw()
  
    ggplot(DataFit2sls, aes(FittedValues, sqrt(abs(StdResiduals)))) + 
      geom_point(na.rm=TRUE) + 
      xlab("Fitted Value") + 
      ylab(expression(sqrt("|Standardized residuals|"))) + 
      ggtitle("Scale-Location") + 
      theme_bw()
    
}

#Proceso -------------------------------------------------------------------------------------------------------------------------------------------------------
#Divide la informacion de acuerdo al nivel de agregacion definido
SPL <- split(Input, paste(Input$HSE, Input$OriginDestination))
#Aplica a cada subset de datos la funcion y une todo en un mismo conjunto de datos
Output <- do.call("rbind", lapply(SPL, OutlierFunction))

