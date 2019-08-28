#Libraries -------------------------------------------------------------------------------------------------------------------------------------------------------
library(data.table)
library(ggplot2)
library(scales)
library(plyr)
library(outliers)
library(mvoutlier)
#Input -------------------------------------------------------------------------------------------------------------------------------------------------------

#Integrated Sales Data (Integrated Sales)
Input <- fread("F:/Dynamic Pricing/Dynamic Pricing/Data/Output_BOGMADRT.csv", header = T, sep = ";")
#Subsets -------------------------------------------------------------------------------------------------------------------------------------------------------

#
Test <- subset(Input, OriginDestination == "BOGMAD" & CabinCode == "Y" & HSE == "Dia Normal" & AdvancePurchase == 1)
#Graficas  -------------------------------------------------------------------------------------------------------------------------------------------------------

#Scatterplot - AveragePrice vs Demand
ggplot(Test, aes(x = AveragePrice, y = Demand)) + 
  geom_point(alpha = I(0.5)) +
  theme_bw(base_size = 20) + 
  theme(axis.line = element_line(colour = "black"),
        panel.grid.major = element_blank(),
        panel.grid.minor = element_blank(),
        panel.border = element_blank(),
        panel.background = element_blank()) +
  scale_x_continuous(breaks = round_any(seq(0, max(Test$AveragePrice), by = 200),200), labels = comma) +
  scale_y_continuous(breaks = round_any(seq(0, max(Test$Demand), by = 2),2), labels = comma) + 
  labs(x = "Average Price", y = "Demand") 

ggplot(Test, aes(x = AveragePrice, y = Demand)) + 
  geom_point(alpha = I(0.5)) +
  geom_smooth(method = "lm") +
  theme_bw(base_size = 20) + 
  theme(axis.line = element_line(colour = "black"),
        panel.grid.major = element_blank(),
        panel.grid.minor = element_blank(),
        panel.border = element_blank(),
        panel.background = element_blank()) +
  scale_x_continuous(breaks = round_any(seq(0, max(Test$AveragePrice), by = 20000),20000), labels = comma) +
  scale_y_continuous(breaks = round_any(seq(0, max(Test$Demand), by = 1),1), labels = comma) + 
  labs(x = "Average Price", y = "Demand") 

#Scatterplot - SeatAvailability vs Demand
ggplot(Test, aes(x = SeatAvailability, y = Demand)) + 
  geom_point(alpha = I(0.5)) +
  theme_bw(base_size = 20) + 
  theme(axis.line = element_line(colour = "black"),
        panel.grid.major = element_blank(),
        panel.grid.minor = element_blank(),
        panel.border = element_blank(),
        panel.background = element_blank()) +
  scale_x_continuous(breaks = round_any(seq(0, max(Test$SeatAvailability), by = 50),50), labels = comma) +
  scale_y_continuous(breaks = round_any(seq(0, max(Test$Demand), by = 1),1), labels = comma) + 
  labs(x = "Seat Availability", y = "Demand") 

#Outliers ----------------------------------------------------------------------------------------------------------------------------------------------------
# MAD
Test$OutliersMAD <- scores(Test$SeatAvailability, type = "mad", prob = 0.95)

ggplot(Test, aes(x = AveragePrice, y = Demand, colour = OutliersMAD)) + 
  geom_point() +
  theme_bw(base_size = 20) + 
  theme(axis.line = element_line(colour = "black"),
        panel.grid.major = element_blank(),
        panel.grid.minor = element_blank(),
        panel.border = element_blank(),
        panel.background = element_blank()) +
  scale_x_continuous(breaks = round_any(seq(0, max(Test$AveragePrice), by = 20000),20000), labels = comma) +
  scale_y_continuous(breaks = round_any(seq(0, max(Test$Demand), by = 1),1), labels = comma) + 
  labs(x = "Average Price", y = "Demand") 

TestWMAD <- subset(Test, OutliersMAD == FALSE)

ggplot(TestWMAD, aes(x = AveragePrice, y = Demand)) + 
  geom_point() +
  geom_smooth(method = "lm") +
  theme_bw(base_size = 20) + 
  theme(axis.line = element_line(colour = "black"),
        panel.grid.major = element_blank(),
        panel.grid.minor = element_blank(),
        panel.border = element_blank(),
        panel.background = element_blank()) +
  scale_x_continuous(breaks = round_any(seq(0, max(TestWMAD$AveragePrice), by = 20000),20000), labels = comma) +
  scale_y_continuous(breaks = round_any(seq(0, max(TestWMAD$Demand), by = 1),1), labels = comma) + 
  labs(x = "Average Price", y = "Demand") 


#Adjusted Quantile Plot
LogOutlierVariables <- data.frame(AveragePrice = log(Test$AveragePrice), SeatAvailability = log(Test$SeatAvailability))
LogOutlierVariables$Outlier <- aq.plot(LogOutlierVariables)$outliers
Test$OutliersAQ <-LogOutlierVariables$Outlier

ggplot(Test, aes(x = AveragePrice, y = Demand, colour = OutliersAQ)) + 
  geom_point() +
  theme_bw(base_size = 20) + 
  theme(axis.line = element_line(colour = "black"),
        panel.grid.major = element_blank(),
        panel.grid.minor = element_blank(),
        panel.border = element_blank(),
        panel.background = element_blank()) +
  scale_x_continuous(breaks = round_any(seq(0, max(Test$AveragePrice), by = 20000),20000), labels = comma) +
  scale_y_continuous(breaks = round_any(seq(0, max(Test$Demand), by = 1),1), labels = comma) + 
  labs(x = "Average Price", y = "Demand") 

TestWAQ <- subset(Test, OutliersAQ == FALSE)

ggplot(TestWAQ, aes(x = AveragePrice, y = Demand)) + 
  geom_point() +
  geom_smooth(method = "lm") +
  theme_bw(base_size = 20) + 
  theme(axis.line = element_line(colour = "black"),
        panel.grid.major = element_blank(),
        panel.grid.minor = element_blank(),
        panel.border = element_blank(),
        panel.background = element_blank()) +
  scale_x_continuous(breaks = round_any(seq(0, max(TestWAQ$AveragePrice), by = 20000),20000), labels = comma) +
  scale_y_continuous(breaks = round_any(seq(0, max(TestWAQ$Demand), by = 1),1), labels = comma) + 
  labs(x = "Average Price", y = "Demand") 

#Multivariate Gaussian Distribution
LogOutlierVariables <- data.frame(AveragePrice = log(Test$AveragePrice), SeatAvailability = log(Test$SeatAvailability))
miu <- matrix(c(mean(LogOutlierVariables$AveragePrice), mean(LogOutlierVariables$SeatAvailability)))
cov <- cov(LogOutlierVariables)

Matrix <- as.matrix(LogOutlierVariables-miu)%*%cov^(-1)%*%(t(as.matrix(LogOutlierVariables-miu)))
Test$OutliersMG <- (1/(sqrt(det(cov)*(2*pi)^2)))*(exp((-1/2)*diag(Matrix))) > 0.99

ggplot(Test, aes(x = AveragePrice, y = Demand, colour = OutliersMG)) + 
  geom_point() +
  theme_bw(base_size = 20) + 
  theme(axis.line = element_line(colour = "black"),
        panel.grid.major = element_blank(),
        panel.grid.minor = element_blank(),
        panel.border = element_blank(),
        panel.background = element_blank()) +
  scale_x_continuous(breaks = round_any(seq(0, max(Test$AveragePrice), by = 30000),30000),labels = comma) +
  scale_y_continuous(breaks = round_any(seq(0, max(Test$Demand), by = 1),1), labels = comma) + 
  labs(x = "Average Price", y = "Demand") 

TestWMG <- subset(Test, OutliersMG == FALSE)

ggplot(TestWMG, aes(x = AveragePrice, y = Demand)) + 
  geom_point() +
  geom_smooth(method = "lm") +
  theme_bw(base_size = 20) + 
  theme(axis.line = element_line(colour = "black"),
        panel.grid.major = element_blank(),
        panel.grid.minor = element_blank(),
        panel.border = element_blank(),
        panel.background = element_blank()) +
  scale_x_continuous(breaks = round_any(seq(0, max(TestWMG$AveragePrice), by = 30000),30000), labels = comma) +
  scale_y_continuous(breaks = round_any(seq(0, max(TestWMG$Demand), by = 1),1), labels = comma) + 
  labs(x = "Average Price", y = "Demand") 

#Tests -------------------------------------------------------------------------------------------------------------------------------------------------------
#Test Normalidad - Average Price
shapiro.test(Test$AveragePrice)
shapiro.test(log(Test$AveragePrice))

#Test Normalidad - Seat Availability
shapiro.test(Test$SeatAvailability)
shapiro.test(log(Test$SeatAvailability))

#Test Normalidad - Demanda 
shapiro.test(Test$Demand)
shapiro.test(log(Test$Demand))
