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
    // 系统ip
	@Value("#{prop.host_ip}")
	private String host_ip;  
    
    @Value("#{prop.zookeeper_ip}")
    private String zookeeper_ip;
    
    @Value("#{prop.zookeeper_port}")
    private String zookeeper_port;
    
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
	// elasticsearch的新index名称
    @Value("#{prop.es_index}")
    private String es_index;
	// elasticsearch的生命周期管理名称
    @Value("#{prop.es_ilm_policy}")
    private String es_ilm_policy;
	// elasticsearch的桶聚合最大值
    @Value("#{prop.es_agg_buckets}")
    private String es_agg_buckets;
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

	public String getEs_ilm_policy() {
		return es_ilm_policy;
	}

	public void setEs_ilm_policy(String es_ilm_policy) {
		this.es_ilm_policy = es_ilm_policy;
	}

	public String getEs_agg_buckets() {
		return es_agg_buckets;
	}

	public void setEs_agg_buckets(String es_agg_buckets) {
		this.es_agg_buckets = es_agg_buckets;
	}
}
