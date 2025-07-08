# OpenMRS Vaccination Schedule Module

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

- `GET /ws/rest/v1/vaccinationschedule/schedule` - List all vaccination schedules
- `GET /ws/rest/v1/vaccinationschedule/schedule/{uuid}` - Get specific schedule
- `POST /ws/rest/v1/vaccinationschedule/schedule` - Create new schedule
- `PUT /ws/rest/v1/vaccinationschedule/schedule/{uuid}` - Update schedule
- `GET /ws/rest/v1/patient/{uuid}/vaccinationstatus` - Get patient vaccination status
- `GET /ws/rest/v1/patient/{uuid}/vaccinationstatus?type=due` - Get due vaccinations
- `GET /ws/rest/v1/patient/{uuid}/vaccinationstatus?type=overdue` - Get overdue vaccinations
- `GET /ws/rest/v1/patient/{uuid}/vaccinationstatus?type=upcoming&days=30` - Get upcoming vaccinations

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

### Creating a Vaccination Schedule

```json
POST /ws/rest/v1/vaccinationschedule/schedule
{
  "name": "Peru National Schedule 2025",
  "description": "Official vaccination schedule for Peru",
  "countryCode": "PER",
  "version": 1,
  "effectiveDate": "2025-01-01",
  "sourceAuthority": "Ministry of Health Peru"
}
```

### Getting Patient Vaccination Status

```javascript
// Get all vaccination statuses for a patient
GET /ws/rest/v1/patient/{patientUuid}/vaccinationstatus

// Get only due vaccinations
GET /ws/rest/v1/patient/{patientUuid}/vaccinationstatus?type=due

// Get overdue vaccinations
GET /ws/rest/v1/patient/{patientUuid}/vaccinationstatus?type=overdue

// Get upcoming vaccinations in next 30 days
GET /ws/rest/v1/patient/{patientUuid}/vaccinationstatus?type=upcoming&days=30
```

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

## License

This module is licensed under the Mozilla Public License 2.0.

## Contributing

Contributions are welcome! Please follow OpenMRS development guidelines and submit pull requests to the main repository.