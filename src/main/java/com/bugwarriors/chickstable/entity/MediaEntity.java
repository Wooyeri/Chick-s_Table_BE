@Entity
@Table(name = "media")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storedFilePath;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;
}