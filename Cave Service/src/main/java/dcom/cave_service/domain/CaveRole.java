package dcom.cave_service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CaveRole {
    private UUID id;
    private UUID caveId;
    private String name;
    private int position;
    private int permissions;

    public void addPermission(int permission) {
        permissions |= permission;
    }

    public void removePermission(int permission) {
        permissions &= ~permission;
    }

    public boolean hasPermission(int permission) {
        return (permissions & permission) == permission;
    }
}
