package org.fffd.l23o6.mapper;

import javax.annotation.processing.Generated;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.vo.user.UserVO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-08T08:16:27+0800",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.7 (Amazon.com Inc.)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public UserVO toUserVO(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserVO userVO = new UserVO();

        userVO.setUsername( userEntity.getUsername() );
        userVO.setName( userEntity.getName() );
        userVO.setPhone( userEntity.getPhone() );
        userVO.setIdn( userEntity.getIdn() );
        userVO.setType( userEntity.getType() );
        userVO.setCredits( userEntity.getCredits() );
        userVO.setDeposit( userEntity.getDeposit() );
        userVO.setUserType( userEntity.getUserType() );

        return userVO;
    }
}
