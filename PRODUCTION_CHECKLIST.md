# OCPP CSMS Production Deployment Checklist

## 🔧 Pre-deployment Setup

### Database Configuration
- [ ] PostgreSQL server is installed and running
- [ ] Database `ocpp_csms` is created
- [ ] Database user has appropriate permissions
- [ ] Database schema is applied (using provided SQL files)
- [ ] Database backups are configured

### Environment Configuration
- [ ] Environment variables are properly set
  - [ ] `DB_URL` - Database connection URL
  - [ ] `DB_USERNAME` - Database username  
  - [ ] `DB_PASSWORD` - Database password (use secrets management)
- [ ] SSL certificates are configured (for production HTTPS/WSS)
- [ ] Firewall rules are configured
  - [ ] Port 8080 for OCPP CSMS API
  - [ ] Port 5432 for PostgreSQL (internal only)
  - [ ] Port 9090 for Prometheus (internal only)
  - [ ] Port 3000 for Grafana (internal only)

### Security Configuration
- [ ] Change default passwords (Grafana: admin/admin)
- [ ] Configure proper authentication for REST APIs
- [ ] Set up HTTPS/WSS for production
- [ ] Configure rate limiting
- [ ] Set up log monitoring and alerts

## 🚀 Deployment Process

### Using Docker (Recommended)
1. [ ] Clone the repository
2. [ ] Configure environment variables in `.env` file
3. [ ] Run `./deploy.sh deploy`
4. [ ] Verify all services are healthy
5. [ ] Test OCPP WebSocket connectivity

### Manual Deployment
1. [ ] Install Java 17+
2. [ ] Install PostgreSQL 12+
3. [ ] Build application: `./mvnw clean package`
4. [ ] Deploy JAR file to server
5. [ ] Configure application.yml
6. [ ] Start application: `java -jar ocpp-csms.jar`

## ✅ Post-deployment Verification

### Health Checks
- [ ] Application health: `curl http://localhost:8080/actuator/health`
- [ ] Database connectivity: Check application logs
- [ ] WebSocket endpoint: `ws://localhost:8080/ocpp/{chargePointId}`
- [ ] All actuator endpoints accessible

### OCPP Protocol Testing
- [ ] Test BootNotification message
- [ ] Test StatusNotification message
- [ ] Test Heartbeat message
- [ ] Test StartTransaction/StopTransaction
- [ ] Test remote commands (RemoteStart/RemoteStop)
- [ ] Test authorization flow

### Performance Testing
- [ ] Load test WebSocket connections
- [ ] Monitor memory usage under load
- [ ] Test database performance
- [ ] Verify log file rotation

### Monitoring Setup
- [ ] Prometheus is collecting metrics
- [ ] Grafana dashboards are configured
- [ ] Alerts are configured for critical metrics
- [ ] Log aggregation is working

## 🔍 Monitoring and Maintenance

### Key Metrics to Monitor
- [ ] WebSocket connections count
- [ ] Active transactions count
- [ ] Database connection pool usage
- [ ] JVM memory usage
- [ ] Response times
- [ ] Error rates

### Regular Maintenance Tasks
- [ ] Review logs for errors
- [ ] Monitor disk space
- [ ] Update dependencies (security patches)
- [ ] Backup database
- [ ] Clean up old log files

### Troubleshooting Endpoints
- Health Check: `GET /actuator/health`
- Metrics: `GET /actuator/metrics`
- Environment: `GET /actuator/env`
- Loggers: `GET /actuator/loggers`

## 📈 Scaling Considerations

### Horizontal Scaling
- [ ] Load balancer configuration
- [ ] Session affinity for WebSocket connections
- [ ] Database connection pooling
- [ ] Shared cache for authorization

### Performance Optimization
- [ ] JVM tuning parameters
- [ ] Database query optimization
- [ ] Connection pooling configuration
- [ ] Caching strategy implementation

## 🔒 Security Hardening

### Application Security
- [ ] Input validation on all endpoints
- [ ] Rate limiting implementation
- [ ] Authentication for management endpoints
- [ ] Secure logging (no sensitive data)

### Infrastructure Security
- [ ] Regular security updates
- [ ] Network segmentation
- [ ] Intrusion detection
- [ ] Log monitoring for security events

## 📞 Emergency Procedures

### Service Recovery
- [ ] Application restart: `./deploy.sh restart`
- [ ] Database recovery procedures documented
- [ ] Rollback procedures documented
- [ ] Incident response plan

### Contact Information
- [ ] On-call engineer contact
- [ ] Database administrator contact
- [ ] Infrastructure team contact

---

**⚠️ Important Notes:**
- Always test in staging environment first
- Keep database backups current
- Monitor logs for the first 24 hours after deployment
- Have rollback plan ready