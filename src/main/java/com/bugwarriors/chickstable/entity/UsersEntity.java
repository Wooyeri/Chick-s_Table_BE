@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;
    private String password;
    private String username;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    private Long mediaId;
}