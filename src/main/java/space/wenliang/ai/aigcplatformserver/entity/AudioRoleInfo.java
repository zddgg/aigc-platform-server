package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class AudioRoleInfo extends AudioModelInfo {

    /**
     *
     */
    @TableField(value = "role")
    public String role;

    /**
     *
     */
    @TableField(value = "gender")
    public String gender;

    /**
     *
     */
    @TableField(value = "age")
    public String age;

    public void setAudioRoleInfo(AudioRoleInfo audioRoleInfo) {
        this.setRole(audioRoleInfo.getRole());
        this.setGender(audioRoleInfo.getGender());
        this.setAge(audioRoleInfo.getAge());
    }
}
