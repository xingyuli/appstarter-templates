FROM docker.mobisist.com/library/nginx:1.11

# Set the timezone.
RUN ln -fs /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && dpkg-reconfigure -f noninteractive tzdata

COPY nginx.conf /etc/nginx/nginx.conf

VOLUME /var/opt/resource
