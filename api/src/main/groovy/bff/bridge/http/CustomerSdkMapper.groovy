package bff.bridge.http

import bff.model.AddBranchOfficeInput
import bff.model.Address
import bff.model.AddressInput
import bff.model.AddressMode
import bff.model.Country
import bff.model.Customer
import bff.model.CustomerStatus
import bff.model.CustomerType
import bff.model.CustomerUpdateInputV2
import bff.model.Day
import bff.model.HourRange
import bff.model.RatingScore
import bff.model.State
import bff.model.StoreType
import bff.model.UpdateBranchOfficeProfileInput
import bff.model.User
import bff.model.VerificationDocument
import bff.model.VerificationDocumentInput
import bff.model.VerificationDocumentType
import bff.model.WorkingDays
import org.springframework.stereotype.Component
import wabi2b.dtos.customers.branchoffice.CreateBranchOfficeAddressDto
import wabi2b.dtos.customers.branchoffice.CreateBranchOfficeRequestDto
import wabi2b.dtos.customers.branchoffice.CreateBranchOfficeUserDto
import wabi2b.dtos.customers.shared.AddressDto
import wabi2b.dtos.customers.shared.CustomerDto
import wabi2b.dtos.customers.shared.DayDto
import wabi2b.dtos.customers.shared.HourRangeDto
import wabi2b.dtos.customers.shared.UpdateCustomerProfileRequestDto
import wabi2b.dtos.customers.shared.VerificationDocumentDto
import wabi2b.dtos.customers.shared.WorkingDaysDto


@Component
class CustomerSdkMapper {

    CreateBranchOfficeRequestDto toDto(AddBranchOfficeInput addBranchOfficeInput){
        return new CreateBranchOfficeRequestDto(
                addBranchOfficeInput.name,
                addBranchOfficeInput.linePhone,
                addBranchOfficeInput.emailVerification?: false,
                null,
                toDto(addBranchOfficeInput.address),
                fromDocInputToDtoDocs(addBranchOfficeInput.verificationDocuments),
                toDto(addBranchOfficeInput.workingDays),
                addBranchOfficeInput.deliveryComment,
                toDtoUser(addBranchOfficeInput)
        )
    }

    CreateBranchOfficeAddressDto toDto(AddressInput addressInput){
        return new CreateBranchOfficeAddressDto(
                addressInput.formatted,
                addressInput.lat.toFloat(),
                addressInput.lon.toFloat(),
                addressInput.state.id,
                addressInput.postalCode,
                addressInput.additionalInfo
        )
    }

    Address toAddress(AddressDto addressDto){
        return new Address(
                id: addressDto.id,
                formatted: addressDto.formatted,
                lat: addressDto.coordinates.lat,
                lon: addressDto.coordinates.lon,
                additionalInfo: addressDto.additionalInfo,
                preferred: addressDto.preferred,
                addressType: AddressMode.valueOf(addressDto.addressType),
                enabled: true,
                state: new State(id: addressDto.state.id),
                postalCode: addressDto.postalCode
        )
    }

    List<Address> toAddressList(List<AddressDto> addressDtos){
        return addressDtos?.collect{
            toAddress(it)
        }
    }

    VerificationDocumentDto toDto(VerificationDocumentInput verificationDocumentInput){
        return new VerificationDocumentDto(
                verificationDocumentInput.id,
                verificationDocumentInput.documentType.name()
        )
    }

    VerificationDocumentDto toDto(VerificationDocument verificationDocument){
        return new VerificationDocumentDto(
                verificationDocument.id,
                verificationDocument.type.name()
        )
    }

    VerificationDocument toVerificationDocument(VerificationDocumentDto verificationDocumentDto){
        return new VerificationDocument(
                id: verificationDocumentDto.id,
                type: VerificationDocumentType.valueOf(verificationDocumentDto.type)
        )
    }

    List<VerificationDocumentDto> fromDocInputToDtoDocs(List<VerificationDocumentInput> verificationDocuments){
        return verificationDocuments?.collect{
            toDto(it)
        }
    }

    List<VerificationDocumentDto> fromDocToDtoDocs(List<VerificationDocument> verificationDocuments){
        return verificationDocuments?.collect{
            toDto(it)
        }
    }

    List<VerificationDocument> toVerificationDocuments(List<VerificationDocumentDto> verificationDocumentDtos){
        return verificationDocumentDtos?.collect{
            toVerificationDocument(it)
        }
    }

    WorkingDaysDto toDto(WorkingDays workingDays){
        return new WorkingDaysDto(
                toDtoDays(workingDays.days),
                toDtoHours(workingDays.hours)
        )
    }

    WorkingDays toWorkingDays(WorkingDaysDto workingDaysDto){
        return new WorkingDays(
                days: toDays(workingDaysDto.days),
                hours: toHourRanges(workingDaysDto.hours)
        )
    }

    DayDto toDto(Day day){
        return new DayDto(day.dayIndex, day.selected)
    }

    Day toDay(DayDto dayDto){
        return new Day(
                dayIndex: dayDto.dayIndex,
                selected: dayDto.selected
        )
    }

    List<DayDto> toDtoDays(List<Day> days){
        return days?.collect{
            toDto(it)
        }
    }

    List<Day> toDays(List<DayDto> dayDtos){
        return dayDtos?.collect{
            toDay(it)
        }
    }

    HourRangeDto toDto(HourRange hourRange){
        return new HourRangeDto(hourRange.from, hourRange.to)
    }

    HourRange toHourRange(HourRangeDto hourRangeDto){
        return new HourRange(
                from: hourRangeDto.from,
                to: hourRangeDto.to
        )
    }

    List<HourRangeDto> toDtoHours(List<HourRange> hourRanges){
        return hourRanges?.collect{
            toDto(it)
        }
    }

    List<HourRange> toHourRanges(List<HourRangeDto> hourRangeDtos){
        return hourRangeDtos?.collect{
            toHourRange(it)
        }
    }

    CreateBranchOfficeUserDto toDtoUser(AddBranchOfficeInput addBranchOfficeInput){
        return new CreateBranchOfficeUserDto(
                addBranchOfficeInput.firstName,
                addBranchOfficeInput.lastName,
                addBranchOfficeInput.countryCode,
                addBranchOfficeInput.phone,
                addBranchOfficeInput.email
        )
    }

    Customer toCustomer(CustomerDto customerDto){
        return new Customer(
                id: customerDto.id,
                name: customerDto.name,
                enabled: customerDto.enabled,
                legalId: customerDto.legalId,
                linePhone: customerDto.phone,
                customerStatus: CustomerStatus.valueOf(customerDto.customerStatus),
                user: new User(id: customerDto.user.id),
                smsVerification: customerDto.smsVerification,
                emailVerification: customerDto.emailVerification,
                customerType: new CustomerType(code: customerDto.customerTypeCode),
                addresses: toAddressList(customerDto.addresses),
                marketingEnabled: customerDto.marketingEnabled,
                workingDays: toWorkingDays(customerDto.workingDays),
                rating: new RatingScore(
                        average: customerDto.rating.average,
                        count: customerDto.rating.count,
                        percentage: customerDto.rating.percentage
                ),
                verificationDocuments: toVerificationDocuments(customerDto.verificationDocuments),
                country_id: customerDto.countryId,
                country: new Country(id: customerDto.countryId),
                storeType: StoreType.valueOf(customerDto.storeType.name()),
                storeOwnerId: customerDto.storeOwnerId,
                permissionOnBranchOffice: customerDto.permissionOnBranchOffice
        )
    }

    UpdateCustomerProfileRequestDto toDto(UpdateBranchOfficeProfileInput updateInput){
        return new UpdateCustomerProfileRequestDto(updateInput.acceptWhatsApp, updateInput.marketingEnabled)
    }

}
