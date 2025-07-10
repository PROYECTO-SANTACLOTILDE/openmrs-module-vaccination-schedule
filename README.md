# PeruHCE Vaccination Schedule Module

A comprehensive OpenMRS module for managing vaccination schedules and tracking patient vaccination status.

## Overview

The Vaccination Schedule Module provides healthcare facilities with the ability to:

- Define and manage vaccination schedules by country and version
- Track patient vaccination status with age-based calculations
- Manage vaccination rules including contraindications and prerequisites
- Assign vaccination schedules to patients
- Monitor due, overdue, and upcoming vaccinations
- Record vaccine administration and track immunization history

## Features

### Core Functionality

- **Vaccination Schedule Management**: Create and manage vaccination schedules with country-specific versions
- **Age-Based Scheduling**: Define minimum, recommended, and maximum ages for each vaccine dose
- **Patient Assignment**: Assign appropriate vaccination schedules to patients based on country/region
- **Status Calculation**: Automatic calculation of vaccination status (PENDING/DUE/OVERDUE/APPLIED/CONTRAINDICATED)
- **Rule Engine**: Define contraindications, prerequisites, and spacing rules between vaccines
- **Dose Sequencing**: Ensure vaccines are administered in proper sequence

### REST API Endpoints

- `GET /ws/rest/v1/vaccinationschedule` - List all vaccination schedules
- `GET /ws/rest/v1/vaccinationschedule/{uuid}` - Get specific schedule
- `POST /ws/rest/v1/vaccinationschedule` - Create new schedule
- `POST /ws/rest/v1/vaccinationschedule/{uuid}` - Update schedule
- `DELETE /ws/rest/v1/vaccinationschedule/{uuid}` - Retire schedule
- `GET /ws/rest/v1/vaccinationstatus?patient={uuid}` - Get patient vaccination status
- `GET /ws/rest/v1/vaccinationstatus?patient={uuid}&type=due` - Get due vaccinations
- `GET /ws/rest/v1/vaccinationstatus?patient={uuid}&type=overdue` - Get overdue vaccinations
- `GET /ws/rest/v1/vaccinationstatus?patient={uuid}&type=upcoming&days=30` - Get upcoming vaccinations

### Data Import Support

The module supports CSV-based data import through the Initializer module:

- **Vaccination Schedules**: `configuration/vaccinationschedules/schedule.csv`
- **Schedule Entries**: `configuration/vaccinationscheduleentries/entries.csv`

## Installation

### Prerequisites

- OpenMRS Platform 2.6.0 or higher
- Initializer Module 2.5.0 or higher
- Web Services REST Module 2.24.0 or higher

### Installation Steps

1. Build the module to produce the .omod file: `mvn clean package`
2. Use the OpenMRS Administration > Manage Modules screen to upload and install the .omod file
3. Start the module
4. Configure vaccination schedules through the REST API or CSV import

If uploads are not allowed from the web, you can drop the .omod into the ~/.OpenMRS/modules folder and restart OpenMRS.

## Configuration

### Global Properties

- `vaccinationschedule.defaultCountryCode`: Default country code (default: PER)
- `vaccinationschedule.autoAssignSchedule`: Auto-assign schedules to new patients (default: true)
- `vaccinationschedule.overdueThresholdDays`: Days after max age for critical overdue (default: 30)
- `vaccinationschedule.reminderDaysAhead`: Days ahead for reminders (default: 7)

### Required Privileges

- **View Vaccination Schedules**: View schedules and patient status
- **Manage Vaccination Schedules**: Create and edit schedules
- **Assign Vaccination Schedules**: Assign schedules to patients
- **Administer Vaccines**: Record vaccine administration

## Usage Examples

## REST API Documentation

### Base URL
```
http://localhost:8080/openmrs/ws/rest/v1/
```

### Authentication
All API endpoints require OpenMRS authentication. Include basic authentication headers or session cookies.

## Field Data Types

### VaccinationSchedule Fields
- `name`: String (required)
- `description`: String  
- `countryCode`: String
- `version`: Integer (not string)
- `effectiveDate`: Date (ISO 8601 format)
- `expiryDate`: Date (ISO 8601 format)
- `sourceAuthority`: String
- `retired`: Boolean

## Vaccination Schedule Management

### 1. Get All Vaccination Schedules
```http
GET /vaccinationschedule
```

**Response:**
```json
{
  "results": [
    {
      "uuid": "schedule-uuid",
      "name": "Peru National Vaccination Schedule",
      "countryCode": "PE",
      "version": 1,
      "effectiveDate": "2024-01-01T00:00:00.000+0000",
      "retired": false,
      "display": "Peru National Vaccination Schedule (PE)"
    }
  ]
}
```

### 2. Get Specific Vaccination Schedule
```http
GET /vaccinationschedule/{uuid}
```

**Example:**
```http
GET /vaccinationschedule/12345678-1234-1234-1234-123456789012
```

### 3. Create New Vaccination Schedule
```http
POST /vaccinationschedule
Content-Type: application/json

{
  "name": "Peru National Schedule 2025",
  "description": "Official vaccination schedule for Peru",
  "countryCode": "PE",
  "version": 1,
  "effectiveDate": "2025-01-01T00:00:00.000+0000",
  "sourceAuthority": "Ministry of Health Peru"
}
```

### 4. Update Vaccination Schedule
```http
POST /vaccinationschedule/{uuid}
Content-Type: application/json

{
  "name": "Updated Schedule Name",
  "description": "Updated description",
  "version": 2
}
```

### 5. Delete (Retire) Vaccination Schedule
```http
DELETE /vaccinationschedule/{uuid}?reason=No longer needed
```

### 6. Search Schedules by Country
```http
GET /vaccinationschedule?country=PE
```

## Patient Vaccination Status

### 1. Get Patient Vaccination Status
```http
GET /vaccinationstatus?patient={patient-uuid}
```

**Example:**
```http
GET /vaccinationstatus?patient=abd061c6-5a72-47e7-8dd0-4d6635d27ca6
```

**Response:**
```json
{
  "results": [
    {
      "vaccineName": "BCG",
      "doseNumber": 1,
      "status": "DUE",
      "dueDate": "2024-01-15T00:00:00.000+0000",
      "appliedDate": null,
      "contraindicationReason": null
    },
    {
      "vaccineName": "Hepatitis B",
      "doseNumber": 1,
      "status": "APPLIED",
      "dueDate": "2024-01-08T00:00:00.000+0000",
      "appliedDate": "2024-01-08T10:30:00.000+0000",
      "contraindicationReason": null
    }
  ]
}
```

### 2. Get Due Vaccinations
```http
GET /vaccinationstatus?patient={patient-uuid}&type=due
```

### 3. Get Overdue Vaccinations
```http
GET /vaccinationstatus?patient={patient-uuid}&type=overdue
```

### 4. Get Upcoming Vaccinations
```http
GET /vaccinationstatus?patient={patient-uuid}&type=upcoming&days=30
```

**Parameters:**
- `days`: Number of days ahead to look (1-365, default: 30)

## Status Values

| Status | Description |
|--------|-------------|
| `PENDING` | Patient too young for vaccination |
| `DUE` | Vaccination is due now |
| `OVERDUE` | Vaccination is overdue |
| `APPLIED` | Vaccination has been administered |
| `CONTRAINDICATED` | Vaccination is contraindicated |

## Error Responses

### 400 Bad Request
```json
{
  "error": {
    "message": "Patient UUID is required",
    "code": "org.openmrs.module.vaccinationschedule.web.rest.PatientVaccinationStatusRestController:92"
  }
}
```

### 404 Not Found
```json
{
  "error": {
    "message": "Patient not found",
    "code": "..."
  }
}
```

## Examples

### Complete Workflow Example

1. **Create a vaccination schedule:**
```bash
curl -X POST "http://localhost:8080/openmrs/ws/rest/v1/vaccinationschedule" \
  -H "Content-Type: application/json" \
  -u admin:Admin123 \
  -d '{
    "name": "Peru Pediatric Schedule",
    "description": "Standard pediatric vaccination schedule for Peru",
    "countryCode": "PE",
    "version": 1,
    "effectiveDate": "2024-01-01T00:00:00.000+0000",
    "sourceAuthority": "MINSA Peru"
  }'
```

2. **Get patient vaccination status:**
```bash
curl "http://localhost:8080/openmrs/ws/rest/v1/vaccinationstatus?patient=abd061c6-5a72-47e7-8dd0-4d6635d27ca6" \
  -u admin:Admin123
```

3. **Get only due vaccinations:**
```bash
curl "http://localhost:8080/openmrs/ws/rest/v1/vaccinationstatus?patient=abd061c6-5a72-47e7-8dd0-4d6635d27ca6&type=due" \
  -u admin:Admin123
```

4. **Get upcoming vaccinations for next 60 days:**
```bash
curl "http://localhost:8080/openmrs/ws/rest/v1/vaccinationstatus?patient=abd061c6-5a72-47e7-8dd0-4d6635d27ca6&type=upcoming&days=60" \
  -u admin:Admin123
```

5. **Search schedules by country:**
```bash
curl "http://localhost:8080/openmrs/ws/rest/v1/vaccinationschedule?country=PE" \
  -u admin:Admin123
```

6. **Get full schedule details:**
```bash
curl "http://localhost:8080/openmrs/ws/rest/v1/vaccinationschedule/12345678-1234-1234-1234-123456789012?v=full" \
  -u admin:Admin123
```

7. **Retire a schedule:**
```bash
curl -X DELETE "http://localhost:8080/openmrs/ws/rest/v1/vaccinationschedule/12345678-1234-1234-1234-123456789012?reason=Replaced%20by%20new%20version" \
  -u admin:Admin123
```

## Testing with curl

### Set up environment variables:
```bash
export OPENMRS_BASE_URL="http://localhost:8080/openmrs/ws/rest/v1"
export OPENMRS_AUTH="admin:Admin123"
export PATIENT_UUID="abd061c6-5a72-47e7-8dd0-4d6635d27ca6"
```

### Test patient vaccination status:
```bash
# Get all vaccination statuses
curl "$OPENMRS_BASE_URL/vaccinationstatus?patient=$PATIENT_UUID" -u $OPENMRS_AUTH

# Get due vaccinations
curl "$OPENMRS_BASE_URL/vaccinationstatus?patient=$PATIENT_UUID&type=due" -u $OPENMRS_AUTH

# Get overdue vaccinations  
curl "$OPENMRS_BASE_URL/vaccinationstatus?patient=$PATIENT_UUID&type=overdue" -u $OPENMRS_AUTH

# Get upcoming vaccinations
curl "$OPENMRS_BASE_URL/vaccinationstatus?patient=$PATIENT_UUID&type=upcoming&days=30" -u $OPENMRS_AUTH
```

### Test vaccination schedule management:
```bash
# Get all schedules
curl "$OPENMRS_BASE_URL/vaccinationschedule" -u $OPENMRS_AUTH

# Search by country
curl "$OPENMRS_BASE_URL/vaccinationschedule?country=PE" -u $OPENMRS_AUTH

# Get specific schedule
curl "$OPENMRS_BASE_URL/vaccinationschedule/YOUR_SCHEDULE_UUID" -u $OPENMRS_AUTH
```

## Integration Notes

- The module integrates with OpenMRS patient management
- Vaccination schedules are country-specific
- Patient vaccination status is calculated in real-time based on age and schedule
- The module supports multiple representations (ref, default, full)
- All timestamps are in ISO 8601 format with timezone information

## Development

To test the API during development:

1. Build the module: `mvn clean package`
2. Deploy the .omod file to OpenMRS
3. **Restart OpenMRS** (required for Hibernate mappings to be loaded)
4. Use the above curl commands or a REST client like Postman
5. Replace `localhost:8080` with your OpenMRS server URL
6. Replace `admin:Admin123` with your OpenMRS credentials
7. Replace patient UUIDs with actual patient UUIDs from your system

**Important**: After deploying the module, you must restart OpenMRS for the Hibernate entity mappings to be properly registered.

## Troubleshooting

### Endpoints not working in production environments

If the REST endpoints work in OpenMRS SDK but not in production/test environments, check:

1. **Module Installation**:
   - Verify the module is installed and started: Admin → Manage Modules
   - Check module status is "Started"

2. **OpenMRS Version Compatibility**:
   - Module requires OpenMRS 2.0.0+ 
   - WebServices REST module must be installed and started

3. **Check Logs**:
   ```bash
   # Look for these log messages in openmrs.log:
   grep "Vaccination Schedule Module" openmrs.log
   grep "VaccinationScheduleRestController" openmrs.log
   ```

4. **Dependencies**:
   - Ensure WebServices REST module is installed
   - Check if module loaded successfully without errors

5. **Test Module Loading**:
   ```bash
   # Check if endpoints are registered:
   curl "http://your-server/openmrs/ws/rest/v1" -u username:password
   # Should list available resources including 'vaccinationschedule'
   ```

6. **Common Issues**:
   - **"Unknown resource"**: Module not loaded or REST controllers not registered
   - **"Unknown entity"**: Hibernate mappings not loaded (restart required)
   - **ClassNotFoundException**: Missing dependencies or module not deployed correctly

### Deployment Checklist

✅ WebServices REST module installed and started
✅ Upload vaccination-schedule-1.0.6.omod file  
✅ Start the module in Admin → Manage Modules
✅ Restart OpenMRS completely
✅ Check logs for any errors during startup
✅ Test endpoint: `GET /ws/rest/v1/vaccinationschedule`

## CSV Import Format

### Vaccination Schedules (vaccinationschedules.csv)

```csv
uuid,name,description,country_code,version,effective_date,expiry_date,source_authority
550e8400-e29b-41d4-a716-446655440000,Peru National Schedule 2025,Official Peru schedule,PER,1,2025-01-01,,Ministry of Health Peru
```

### Schedule Entries (vaccinationscheduleentries.csv)

```csv
uuid,schedule_uuid,vaccine_concept,dose_number,age_min_days,age_recommended_days,age_max_days,interval_from_previous_days,is_mandatory,sort_order
entry-uuid-1,550e8400-e29b-41d4-a716-446655440000,bcg-concept-uuid,1,0,1,30,,true,1
entry-uuid-2,550e8400-e29b-41d4-a716-446655440000,hepatitis-b-concept-uuid,1,0,1,30,,true,2
```

## Database Schema

The module creates the following tables:

- `vaccination_schedule`: Main schedule definitions
- `vaccination_schedule_entry`: Individual vaccine entries within schedules
- `vaccination_schedule_rule`: Business rules (contraindications, prerequisites)
- `patient_vaccination_schedule`: Patient-schedule assignments

## Business Logic

### Age Calculation

Patient ages are calculated in days from birth date to provide precise scheduling for infant vaccinations.

### Status Calculation Algorithm

```
if (vaccineAlreadyAdministered) return APPLIED;
if (ageInDays < entry.getAgeInDaysMin()) return PENDING;
if (ageInDays <= entry.getAgeInDaysMax()) return DUE;
return OVERDUE;
```

### Dose Sequencing

The system ensures proper dose sequencing by validating that previous doses have been administered before allowing subsequent doses.

## Building from Source

### Requirements

- Java 8+ 
- Maven 3.x+

### Build Commands

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the module
mvn clean package

# Apply code formatting
mvn spotless:apply

# Check code formatting
mvn spotless:check
```

The .omod file will be in the `omod/target` folder after running `mvn package`.

### Code Formatting

This project uses Spotless for code formatting. Spotless runs automatically during the build process. You can also run it manually:

- `mvn spotless:apply` - Format your code
- `mvn spotless:check` - Check formatting without making changes

## Development

### Module Structure

```
api/                          # Core business logic
├── src/main/java/
│   └── org/openmrs/module/vaccinationschedule/
│       ├── VaccinationSchedule.java
│       ├── VaccinationScheduleEntry.java
│       ├── PatientVaccinationStatus.java
│       ├── api/VaccinationScheduleService.java
│       ├── exception/
│       └── initializer/
├── src/main/resources/
│   └── liquibase.xml         # Database schema
└── src/test/java/            # Unit tests

omod/                         # Web module
├── src/main/java/
│   └── org/openmrs/module/vaccinationschedule/
│       └── web/rest/         # REST controllers
├── src/main/resources/
│   └── config.xml            # Module configuration
└── src/main/webapp/          # Web resources
```

### Key Classes

- **VaccinationSchedule**: Main entity for vaccination schedules
- **VaccinationScheduleEntry**: Individual vaccine doses within a schedule
- **PatientVaccinationStatus**: Computed vaccination status for patients
- **VaccinationScheduleService**: Core business logic service
- **VaccinationScheduleRestController**: REST API endpoints

## Support

- **Documentation**: [OpenMRS Wiki](https://wiki.openmrs.org)
- **Community**: [OpenMRS Talk](https://talk.openmrs.org)
- **Issues**: [GitHub Issues](https://github.com/openmrs/openmrs-module-vaccination-schedule/issues)


## Contributing

Contributions are welcome! Please follow OpenMRS development guidelines and submit pull requests to the main repository.