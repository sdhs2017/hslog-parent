package com.jz.bigdata.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component("configProperty")
@Configuration
public class ConfigProperty {

	// 操作系统的用户名（elsearch）
	@Value("#{prop.host_user}")
	private String host_user;

	// 操作系统的密码
	@Value("#{prop.host_passwd}")
	private String host_passwd;

	// 日志服务器ip
	@Value("#{prop.host_ip}")
	private String host_ip;  

	// zookeeper的ip
    @Value("#{prop.zookeeper_ip}")
    private String zookeeper_ip;

	// zookeeper端口
    @Value("#{prop.zookeeper_port}")
    private String zookeeper_port;

	// zookeeper地址
    @Value("#{prop.zookeeper_path}")
    private String zookeeper_path;
    
    @Value("#{prop.es_ip}")
    private String es_ip;
    
    @Value("#{prop.es_port}")
    private String es_port;

	// elasticsearch的用户名
	@Value("#{prop.es_user}")
	private String es_user;

	// elasticsearch的密码
	@Value("#{prop.es_password}")
	private String es_password;
    
    @Value("#{prop.es_path}")
    private String es_path;
    
    @Value("#{prop.es_path_snapshot}")
    private String es_path_snapshot;

	@Value("#{prop.es_templatename}")
	private String es_templatename;

	@Value("#{prop.es_tempalatePattern}")
	private String es_tempalatePattern;

	// elasticsearch的旧版本index名称
	@Value("#{prop.es_old_index}")
	private String es_old_index;

	// elasticsearch的镜像口流量的index名称
	@Value("#{prop.es_flow_index}")
	private String es_flow_index;

	// elasticsearch的新index名称
    @Value("#{prop.es_index}")
    private String es_index;

	// elasticsearch的生命周期管理名称
	@Value("#{prop.es_ilm_policy}")
	private String es_ilm_policy;

	// elasticsearch的索引保留时间
	@Value("#{prop.es_days_of_log_storage}")
	private String es_days_of_log_storage;

	@Value("#{prop.es_repository_name}")
	private String es_repository_name;

	@Value("#{prop.es_repository_path}")
	private String es_repository_path;

	@Value("#{prop.es_max_result_window}")
	private String es_max_result_window;

	@Value("#{prop.es_number_of_replicas}")
	private String es_number_of_replicas;

	@Value("#{prop.es_number_of_shards}")
	private String es_number_of_shards;
    
    @Value("#{prop.es_bulk}")
    private Integer es_bulk;
    
    @Value("#{prop.email_address}")
    private String email_address;
    
    @Value("#{prop.number}")
    private String number;
    
    @Value("#{prop.masscan_path}")
    private String masscan_path;
    
    @Value("#{prop.masscan_ports}")
    private String masscan_ports;
    
    @Value("#{prop.protocol}")
    private String protocol;
    
    @Value("#{prop.pcap4j_network}")
    private String pcap4j_network;

	@Value("#{prop.es_search_max_buckets}")
    private String es_search_max_buckets;

	@Value("#{prop.es_max_shards_per_node}")
	private String es_max_shards_per_node;

	@Value("#{prop.es_mapping_total_fields_limit}")
	private String es_mapping_total_fields_limit;

	// 需要合并的index
	@Value("#{prop.es_merge_index}")
    private String es_merge_index;

	// 合并后index的segments的个数
	@Value("#{prop.es_number_of_segment}")
	private String es_number_of_segment;

	@Value("#{prop.echart_default_points}")
	private String echart_default_points;

	// 修改ip地址的脚本路径
	@Value("#{prop.modifyip_path}")
	private String modifyip_path;
	// 修改ip地址的脚本名称
	@Value("#{prop.modifyip_shell}")
	private String modifyip_shell;
	//kafka地址
	@Value("#{prop.kafka_path}")
	private String kafka_path;
	//kafka批量消费数
	@Value("#{prop.kafka_max_poll_records}")
	private String kafka_max_poll_records;
	//告警模块示例数据默认查询的时间范围
	@Value("#{prop.alert_default_area}")
	private String alert_default_area;
	//告警模块的template
	@Value("#{prop.es_alert_templatePattern}")
	private String es_alert_templatePattern;
	//告警模块的index
	@Value("#{prop.es_alert_index}")
	private String es_alert_index;
	//文件日志index
	@Value("#{prop.es_file_index}")
	private String es_file_index;
	//指标日志index
	@Value("#{prop.es_metric_index}")
	private String es_metric_index;
	//流量日志index
	@Value("#{prop.es_packet_index}")
	private String es_packet_index;

	//数据可视化模块 index
	@Value("#{prop.es_hsdata_index}")
	private String es_hsdata_index;
	//filebeat收取cvs/json日志对应前缀
	@Value("#{prop.es_filelog_pre}")
	private String es_filelog_pre;
	//#数据源管理-数据预览条数
	@Value("#{prop.data_source_preview_num}")
	private Integer data_source_preview_num;
	//首页
	@Value("#{prop.homepage_url}")
	private String homepage_url;

	//产品信息
	@Value("#{prop.product_info}")
	private String product_info;

	@Value("#{prop.kafka_topic_syslog}")
	private String kafka_topic_syslog;
	@Value("#{prop.kafka_topic_beats}")
	private String kafka_topic_beats;
	@Value("#{prop.kafka_topic_file}")
	private String kafka_topic_file;

	public String getKafka_topic_syslog() {
		return kafka_topic_syslog;
	}

	public void setKafka_topic_syslog(String kafka_topic_syslog) {
		this.kafka_topic_syslog = kafka_topic_syslog;
	}

	public String getKafka_topic_beats() {
		return kafka_topic_beats;
	}

	public void setKafka_topic_beats(String kafka_topic_beats) {
		this.kafka_topic_beats = kafka_topic_beats;
	}

	public String getKafka_topic_file() {
		return kafka_topic_file;
	}

	public void setKafka_topic_file(String kafka_topic_file) {
		this.kafka_topic_file = kafka_topic_file;
	}

	public String getProduct_info() {
		return product_info;
	}

	public void setProduct_info(String product_info) {
		this.product_info = product_info;
	}

	public String getHomepage_url() {
		return homepage_url;
	}

	public void setHomepage_url(String homepage_url) {
		this.homepage_url = homepage_url;
	}

	public Integer getData_source_preview_num() {
		return data_source_preview_num;
	}

	public void setData_source_preview_num(Integer data_source_preview_num) {
		this.data_source_preview_num = data_source_preview_num;
	}

	public String getEs_filelog_pre() {
		return es_filelog_pre;
	}

	public void setEs_filelog_pre(String es_filelog_pre) {
		this.es_filelog_pre = es_filelog_pre;
	}

	public String getEs_hsdata_index() {
		return es_hsdata_index;
	}

	public void setEs_hsdata_index(String es_hsdata_index) {
		this.es_hsdata_index = es_hsdata_index;
	}

	public String getEs_metric_index() {
		return es_metric_index;
	}

	public void setEs_metric_index(String es_metric_index) {
		this.es_metric_index = es_metric_index;
	}

	public String getEs_packet_index() {
		return es_packet_index;
	}

	public void setEs_packet_index(String es_packet_index) {
		this.es_packet_index = es_packet_index;
	}

	public String getEs_file_index() {
		return es_file_index;
	}

	public void setEs_file_index(String es_file_index) {
		this.es_file_index = es_file_index;
	}

	public String getEs_alert_templatePattern() {
		return es_alert_templatePattern;
	}

	public void setEs_alert_templatePattern(String es_alert_templatePattern) {
		this.es_alert_templatePattern = es_alert_templatePattern;
	}

	public String getEs_alert_index() {
		return es_alert_index;
	}

	public void setEs_alert_index(String es_alert_index) {
		this.es_alert_index = es_alert_index;
	}

	public String getAlert_default_area() {
		return alert_default_area;
	}

	public void setAlert_default_area(String alert_default_area) {
		this.alert_default_area = alert_default_area;
	}


	public String getKafka_path() {
		return kafka_path;
	}

	public void setKafka_path(String kafka_path) {
		this.kafka_path = kafka_path;
	}

	public String getKafka_max_poll_records() {
		return kafka_max_poll_records;
	}

	public void setKafka_max_poll_records(String kafka_max_poll_records) {
		this.kafka_max_poll_records = kafka_max_poll_records;
	}

	public String getEchart_default_points() {
		return echart_default_points;
	}

	public void setEchart_default_points(String echart_default_points) {
		this.echart_default_points = echart_default_points;
	}

	public String getEs_mapping_total_fields_limit() {
		return es_mapping_total_fields_limit;
	}

	public void setEs_mapping_total_fields_limit(String es_mapping_total_fields_limit) {
		this.es_mapping_total_fields_limit = es_mapping_total_fields_limit;
	}

	public String getEs_max_shards_per_node() {
		return es_max_shards_per_node;
	}

	public void setEs_max_shards_per_node(String es_max_shards_per_node) {
		this.es_max_shards_per_node = es_max_shards_per_node;
	}

	public String getEs_search_max_buckets() {
		return es_search_max_buckets;
	}

	public void setEs_search_max_buckets(String es_search_max_buckets) {
		this.es_search_max_buckets = es_search_max_buckets;
	}

	public String getHost_user() {
		return host_user;
	}

	public void setHost_user(String host_user) {
		this.host_user = host_user;
	}

	public String getHost_passwd() {
		return host_passwd;
	}

	public void setHost_passwd(String host_passwd) {
		this.host_passwd = host_passwd;
	}

	public String getHost_ip() {
		return host_ip;
	}

	public void setHost_ip(String host_ip) {
		this.host_ip = host_ip;
	}

	public String getZookeeper_ip() {
		return zookeeper_ip;
	}

	public void setZookeeper_ip(String zookeeper_ip) {
		this.zookeeper_ip = zookeeper_ip;
	}

	public String getZookeeper_port() {
		return zookeeper_port;
	}

	public void setZookeeper_port(String zookeeper_port) {
		this.zookeeper_port = zookeeper_port;
	}

	public String getZookeeper_path() {
		return zookeeper_path;
	}

	public void setZookeeper_path(String zookeeper_path) {
		this.zookeeper_path = zookeeper_path;
	}

	public String getEs_ip() {
		return es_ip;
	}

	public void setEs_ip(String es_ip) {
		this.es_ip = es_ip;
	}

	public String getEs_port() {
		return es_port;
	}

	public void setEs_port(String es_port) {
		this.es_port = es_port;
	}

	public String getEs_user() {
		return es_user;
	}

	public void setEs_user(String es_user) {
		this.es_user = es_user;
	}

	public String getEs_password() {
		return es_password;
	}

	public void setEs_password(String es_password) {
		this.es_password = es_password;
	}

	public String getEs_path() {
		return es_path;
	}

	public void setEs_path(String es_path) {
		this.es_path = es_path;
	}

	public String getEs_path_snapshot() {
		return es_path_snapshot;
	}

	public void setEs_path_snapshot(String es_path_snapshot) {
		this.es_path_snapshot = es_path_snapshot;
	}

	public String getEs_repository_name() {
		return es_repository_name;
	}

	public void setEs_repository_name(String es_repository_name) {
		this.es_repository_name = es_repository_name;
	}

	public String getEs_repository_path() {
		return es_repository_path;
	}

	public void setEs_repository_path(String es_repository_path) {
		this.es_repository_path = es_repository_path;
	}

	public String getEs_max_result_window() {
		return es_max_result_window;
	}

	public void setEs_max_result_window(String es_max_result_window) {
		this.es_max_result_window = es_max_result_window;
	}

	public String getEs_number_of_replicas() {
		return es_number_of_replicas;
	}

	public void setEs_number_of_replicas(String es_number_of_replicas) {
		this.es_number_of_replicas = es_number_of_replicas;
	}

	public String getEs_number_of_shards() {
		return es_number_of_shards;
	}

	public void setEs_number_of_shards(String es_number_of_shards) {
		this.es_number_of_shards = es_number_of_shards;
	}

	public String getEs_templatename() {
		return es_templatename;
	}

	public void setEs_templatename(String es_templatename) {
		this.es_templatename = es_templatename;
	}

	public String getEs_tempalatePattern() {
		return es_tempalatePattern;
	}

	public void setEs_tempalatePattern(String es_tempalatePattern) {
		this.es_tempalatePattern = es_tempalatePattern;
	}

	public String getEs_old_index() {
		return es_old_index;
	}

	public void setEs_old_index(String es_old_index) {
		this.es_old_index = es_old_index;
	}

	public String getEs_flow_index() {
		return es_flow_index;
	}

	public void setEs_flow_index(String es_flow_index) {
		this.es_flow_index = es_flow_index;
	}

	public String getEs_index() {
		return es_index;
	}

	public void setEs_index(String es_index) {
		this.es_index = es_index;
	}

	public Integer getEs_bulk() {
		return es_bulk;
	}

	public void setEs_bulk(Integer es_bulk) {
		this.es_bulk = es_bulk;
	}

	public String getEmail_address() {
		return email_address;
	}

	public void setEmail_address(String email_address) {
		this.email_address = email_address;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMasscan_path() {
		return masscan_path;
	}

	public void setMasscan_path(String masscan_path) {
		this.masscan_path = masscan_path;
	}

	public String getMasscan_ports() {
		return masscan_ports;
	}

	public void setMasscan_ports(String masscan_ports) {
		this.masscan_ports = masscan_ports;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getPcap4j_network() {
		return pcap4j_network;
	}

	public void setPcap4j_network(String pcap4j_network) {
		this.pcap4j_network = pcap4j_network;
	}

	public String getEs_days_of_log_storage() {
		return es_days_of_log_storage;
	}

	public void setEs_days_of_log_storage(String es_days_of_log_storage) {
		this.es_days_of_log_storage = es_days_of_log_storage;
	}

	public String getEs_merge_index() {
		return es_merge_index;
	}

	public void setEs_merge_index(String es_merge_index) {
		this.es_merge_index = es_merge_index;
	}

	public String getEs_number_of_segment() {
		return es_number_of_segment;
	}

	public void setEs_number_of_segment(String es_number_of_segment) {
		this.es_number_of_segment = es_number_of_segment;
	}

	public String getEs_ilm_policy() {
		return es_ilm_policy;
	}

	public void setEs_ilm_policy(String es_ilm_policy) {
		this.es_ilm_policy = es_ilm_policy;
	}

	public String getModifyip_path() {
		return modifyip_path;
	}

	public void setModifyip_path(String modifyip_path) {
		this.modifyip_path = modifyip_path;
	}

	public String getModifyip_shell() {
		return modifyip_shell;
	}

	public void setModifyip_shell(String modifyip_shell) {
		this.modifyip_shell = modifyip_shell;
	}
}
