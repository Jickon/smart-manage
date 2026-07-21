INSERT INTO public.t_sys_user (
    id,
    username,
    password,
    nickname,
    enabled,
    create_time,
    update_time,
    version
)
VALUES (
    1,
    'administrator',
    '$argon2i$v=19$m=65536,t=2,p=1$YX7MmbacZUT02bWnUBFzLQ$yhCo/keSWZm4rO7TQ60+9WsnaQoXNWM7/I6TYGXlQgw',
    '管理员',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
)
ON CONFLICT (username) DO NOTHING;

