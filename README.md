# Bachelorarbeit_Load_Aware_Change_Detection

Thesis question: To what extent can incorporating system-level information, such as CPU utilization, enable adaptive threshold-based change detection to utilize available computational resources more effectively in order to maximize data accuracy under varying workload conditions?


To implement: 
1. Static Thresholding (CUSUM, self-implemented in python)
2. Adaptive Thresholding (Mukherjee et. Al., Real-time adaptation of decision thresholds in sensor networks for detection of moving targets, self-implemented core concepts)
3. Load-Aware Adaptive Thresholding (threshhold is additionally dependent on the utilization of the receiver - linearly/exponentially)


System Design and Experiment:

Sender/Device <=> Receiver

Sender sends data continously with different Change Detection mechanisms (1. / 2. / 3.).

Receiver has constant resources, but will receive load spikes during the experiment.

Measurements during the experiment:
- Reproduction error of original and received data stream
- Latency
- Throughput
