# Bachelorarbeit_Load_Aware_Change_Detection

Thesis question: To what extent can incorporating system-level information, such as CPU utilization, enable adaptive threshold-based change detection to utilize available computational resources more effectively in order to maximize data accuracy under varying workload conditions?


To implement: 
1. CUSUM (static approach)
2. Adaptive Thresholding (Mukherjee et. Al., Real-time adaptation of decision thresholds in sensor networks for detection of moving targets, self-implemented core concepts)
3. Load-Aware Adaptive Thresholding (threshhold is additionally dependent on the utilization of the receiver - linearly/exponentially)


System Design and Experiments:

Experiments: 
1. weather sensor data (https://db.csail.mit.edu/labdata/labdata.html - target = temperature)
2. machine data (https://archive.ics.uci.edu/dataset/791/metropt+3+dataset - target = oil_temperature)

Sender/Sensor <=> Receiver

For each experiment, the sender will implement each Change Detection mechanism once (1., 2., and 3.). 

Both have constant resources, but the receiver will experience different loads (low, medium, high) during the experiment.

During the experiment, there will be measured:
- Reproduction error of original and received data stream
- Latency
- Throughput

For the load-aware mechanism, the Sender will frequently fetch the CPU utilization and the queue size of the Receiver by calling an endpoint in the receiver.

The sender and the receiver will be run as a docker container. The implementation will be in Java and the containers will communicate via gRPC.


